package mcjty.lib.gui;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import mcjty.lib.varia.Logging;
import org.apache.logging.log4j.util.Strings;

public class GuiParser {
   @Nullable
   private static GuiParser.GuiCommand parseCommand(StreamTokenizer tokenizer) throws IOException, GuiParser.ParserException {
      int token = tokenizer.nextToken();
      if (token == -1) {
         return null;
      } else if (token != -3) {
         throw new GuiParser.ParserException("Expected a command token, got a '" + (char)token + "' instead!", tokenizer.lineno());
      } else {
         GuiParser.GuiCommand guiCommand = new GuiParser.GuiCommand(tokenizer.sval);
         token = tokenizer.nextToken();
         if (token == 40) {
            token = tokenizer.nextToken();

            while (token != 41) {
               if (token != -3 && token != 39) {
                  if (token != -2) {
                     throw new GuiParser.ParserException("Expected parameter! Got '" + (char)token + "' instead", tokenizer.lineno());
                  }

                  guiCommand.parameter((int)tokenizer.nval);
               } else if (token == -3 && "true".equals(tokenizer.sval.toLowerCase())) {
                  guiCommand.parameter(true);
               } else if (token == -3 && "false".equals(tokenizer.sval.toLowerCase())) {
                  guiCommand.parameter(false);
               } else {
                  guiCommand.parameter(tokenizer.sval);
               }

               token = tokenizer.nextToken();
               if (token == 44) {
                  token = tokenizer.nextToken();
               }
            }

            token = tokenizer.nextToken();
         }

         if (token != 123) {
            tokenizer.pushBack();
         } else {
            while (token != 125) {
               if (token != 123) {
                  tokenizer.pushBack();
               }

               guiCommand.command(parseCommand(tokenizer));
               token = tokenizer.nextToken();
            }
         }

         return guiCommand;
      }
   }

   public static List<GuiParser.GuiCommand> parse(Reader reader) throws IOException, GuiParser.ParserException {
      StreamTokenizer tokenizer = new StreamTokenizer(reader);
      tokenizer.slashSlashComments(true);
      tokenizer.eolIsSignificant(false);
      tokenizer.quoteChar(39);
      tokenizer.parseNumbers();
      List<GuiParser.GuiCommand> commands = new ArrayList<>();

      for (GuiParser.GuiCommand command = parseCommand(tokenizer); command != null; command = parseCommand(tokenizer)) {
         commands.add(command);
      }

      return commands;
   }

   public static void main(String[] args) {
      StringReader reader = new StringReader(
         "                panel() {\n            layout(positional)\n            bgimage('rftools:textures/gui/securitymanager.png')\n            widgetlist('players') {\n                bgthickness(-1)\n                bgfilled1(-7631989)\n            }\n            slider() {\n                scrollable('players')\n                desiredwidth(10)\n            }\n        }"
      );

      try {
         parse(reader).forEach(command -> command.dump(1));
      } catch (GuiParser.ParserException | IOException var3) {
         Logging.logError("Error parsing!", var3);
      }
   }

   public static <T> void put(GuiParser.GuiCommand parent, String name, T value, T def) {
      if (value != null) {
         if (!value.equals(def)) {
            parent.command(new GuiParser.GuiCommand(name).parameter(value));
         }
      }
   }

   public static <T> T get(GuiParser.GuiCommand parent, String name, T def) {
      return parent.findCommand(name).map(cmd -> cmd.getOptionalPar(0, def)).orElse(def);
   }

   public static <T> T getIndexed(GuiParser.GuiCommand parent, String name, int idx, T def) {
      return parent.findCommand(name).map(cmd -> cmd.getOptionalPar(idx, def)).orElse(def);
   }

   public static class GuiCommand {
      private final String id;
      private final List<Object> parameters = new ArrayList<>();
      private final List<GuiParser.GuiCommand> guiCommands = new ArrayList<>();
      private final Map<String, GuiParser.GuiCommand> commandMap = new HashMap<>();

      public GuiCommand(String id) {
         this.id = id;
      }

      public String getId() {
         return this.id;
      }

      public GuiParser.GuiCommand parameter(Object parameter) {
         this.parameters.add(parameter);
         return this;
      }

      public GuiParser.GuiCommand command(GuiParser.GuiCommand guiCommand) {
         this.guiCommands.add(guiCommand);
         this.commandMap.put(guiCommand.getId(), guiCommand);
         return this;
      }

      public List<Object> getParameters() {
         return this.parameters;
      }

      public List<GuiParser.GuiCommand> getGuiCommands() {
         return this.guiCommands;
      }

      public Optional<GuiParser.GuiCommand> findCommand(String cmd) {
         return Optional.ofNullable(this.commandMap.get(cmd));
      }

      public Stream<GuiParser.GuiCommand> commands() {
         return this.guiCommands.stream();
      }

      public void removeParameter(int index) {
         this.parameters.remove(index);
      }

      public Stream<Object> parameters() {
         return this.parameters.stream();
      }

      public <T> T getOptionalPar(int par, T def) {
         return (T)(par >= this.getParameters().size() ? def : this.getParameters().get(par));
      }

      @Override
      public String toString() {
         return "Command{id='" + this.id + "', parameters=" + this.parameters + ", commands=" + this.guiCommands + "}";
      }

      private static String ind(int i) {
         return "                                                                        ".substring(0, i);
      }

      public void write(PrintWriter writer, int indent) {
         writer.print(ind(indent) + this.id);
         if (!this.parameters.isEmpty() && this.parameters().anyMatch(o -> o != null && !"".equals(o))) {
            writer.print('(');
            String comma = "";

            for (Object parameter : this.parameters) {
               Object par = parameter;
               if (parameter instanceof String) {
                  par = Strings.quote((String)parameter);
               }

               writer.print(comma + par);
               comma = ",";
            }

            writer.print(')');
         }

         if (this.guiCommands.isEmpty()) {
            writer.println("");
         } else {
            writer.println(" {");

            for (GuiParser.GuiCommand cmd : this.guiCommands) {
               cmd.write(writer, indent + 4);
            }

            writer.println(ind(indent) + "}");
         }
      }

      public void dump(int indent) {
         System.out.print(ind(indent) + this.id + "(");
         String comma = "";

         for (Object parameter : this.parameters) {
            Object par = parameter;
            if (parameter instanceof String) {
               par = Strings.quote((String)parameter);
            }

            System.out.print(comma + par);
            comma = ",";
         }

         System.out.print(")");
         if (this.guiCommands.isEmpty()) {
            System.out.println("");
         } else {
            System.out.println(" {");

            for (GuiParser.GuiCommand cmd : this.guiCommands) {
               cmd.dump(indent + 4);
            }

            System.out.println(ind(indent) + "}");
         }
      }
   }

   public static class ParserException extends Exception {
      public ParserException(String s, int linenr) {
         super(s + " (line " + linenr + ")");
      }
   }
}
