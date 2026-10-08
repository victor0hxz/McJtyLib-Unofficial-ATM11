package mcjty.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import mcjty.lib.base.GeneralConfig;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.network.IServerCommand;
import mcjty.lib.network.Networking;
import mcjty.lib.preferences.PreferencesProperties;
import mcjty.lib.setup.ClientSetup;
import mcjty.lib.setup.ModSetup;
import mcjty.lib.setup.Registration;
import mcjty.lib.typed.TypedMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import org.apache.commons.lang3.tuple.Pair;

@Mod("mcjtylib")
public class McJtyLib {
   public static final String MODID = "mcjtylib";
   public static final ModSetup setup = new ModSetup();
   public static McJtyLib instance;
   private static final Map<Pair<String, String>, IServerCommand> serverCommands = new HashMap<>();
   private static final Map<Pair<String, String>, IServerCommand> clientCommands = new HashMap<>();
   private static final Map<String, CommandInfo> commandInfos = new HashMap<>();

   public McJtyLib(ModContainer container, IEventBus bus, Dist dist) {
      instance = this;
      Registration.init(bus);
      bus.addListener(setup::init);
      bus.addListener(Networking::registerMessages);
      bus.addListener(GeneralConfig::onLoad);
      bus.addListener(GeneralConfig::onFileChange);
      if (dist.isClient()) {
         bus.addListener(Networking::registerClientMessages);
         bus.addListener(ClientSetup::init);
         bus.addListener(ClientSetup::registerKeyBinds);
         bus.addListener(ClientSetup::registerClientComponentTooltips);
      }

      container.registerConfig(Type.CLIENT, GeneralConfig.CLIENT_CONFIG);
      container.registerConfig(Type.SERVER, GeneralConfig.SERVER_CONFIG);
   }

   public static <T> void registerListCommandInfo(
      String command, Class<T> type, Function<FriendlyByteBuf, T> deserializer, BiConsumer<FriendlyByteBuf, T> serializer
   ) {
      commandInfos.put(command, new CommandInfo<>(type, deserializer, serializer));
   }

   public static CommandInfo getCommandInfo(String command) {
      return commandInfos.get(command);
   }

   public static void registerCommand(String modid, String id, IServerCommand command) {
      serverCommands.put(Pair.of(modid, id), command);
   }

   public static void registerClientCommand(String modid, String id, IServerCommand command) {
      clientCommands.put(Pair.of(modid, id), command);
   }

   public static boolean handleCommand(String modid, String id, Player player, TypedMap arguments) {
      IServerCommand command = serverCommands.get(Pair.of(modid, id));
      return command == null ? false : command.execute(player, arguments);
   }

   public static boolean handleClientCommand(String modid, String id, Player player, TypedMap arguments) {
      IServerCommand command = clientCommands.get(Pair.of(modid, id));
      return command == null ? false : command.execute(player, arguments);
   }

   public static PreferencesProperties getPreferencesProperties(Player player) {
      return (PreferencesProperties)player.getData(Registration.PREFERENCES_PROPERTIES);
   }

   public static void setPreferencesProperties(Player player, PreferencesProperties properties) {
      player.setData(Registration.PREFERENCES_PROPERTIES, properties);
   }
}
