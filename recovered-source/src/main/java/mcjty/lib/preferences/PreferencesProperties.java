package mcjty.lib.preferences;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nonnull;
import mcjty.lib.gui.BuffStyle;
import mcjty.lib.gui.GuiStyle;
import mcjty.lib.network.Networking;
import mcjty.lib.network.PacketSendPreferencesToClient;
import net.minecraft.server.level.ServerPlayer;

public class PreferencesProperties {
   private static final int DEFAULT_BUFFX = -20;
   private static final int DEFAULT_BUFFY = -20;
   private static final GuiStyle DEFAULT_STYLE = GuiStyle.STYLE_FLAT_GRADIENT;
   private int buffX = -20;
   private int buffY = -20;
   private BuffStyle buffStyle = BuffStyle.BOTRIGHT;
   private GuiStyle style = DEFAULT_STYLE;
   public static final Codec<PreferencesProperties> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
            BuffStyle.CODEC.fieldOf("buffStyle").forGetter(PreferencesProperties::getBuffStyle),
            Codec.INT.fieldOf("buffX").forGetter(PreferencesProperties::getBuffX),
            Codec.INT.fieldOf("buffY").forGetter(PreferencesProperties::getBuffY),
            GuiStyle.CODEC.fieldOf("style").forGetter(PreferencesProperties::getStyle)
         )
         .apply(instance, PreferencesProperties::new)
   );
   private boolean dirty = true;

   public PreferencesProperties(BuffStyle buffStyle, int buffX, int buffY, GuiStyle style) {
      this.buffStyle = buffStyle;
      this.buffX = buffX;
      this.buffY = buffY;
      this.style = style;
   }

   public PreferencesProperties() {
   }

   public void tick(ServerPlayer player) {
      if (this.dirty) {
         this.syncToClient(player);
      }
   }

   private void syncToClient(ServerPlayer player) {
      Networking.sendToPlayer(PacketSendPreferencesToClient.create(this.buffStyle, this.buffX, this.buffY, this.style), player);
      this.dirty = false;
   }

   public void reset() {
      this.buffStyle = BuffStyle.TOPLEFT;
      this.buffX = -20;
      this.buffY = -20;
      this.style = DEFAULT_STYLE;
      this.dirty = true;
   }

   public boolean setStyle(String s) {
      GuiStyle st = GuiStyle.getStyle(s);
      if (st == null) {
         return false;
      } else {
         this.style = st;
         this.dirty = true;
         return true;
      }
   }

   public boolean setStyle(GuiStyle st) {
      if (st == null) {
         return false;
      } else {
         this.style = st;
         this.dirty = true;
         return true;
      }
   }

   @Nonnull
   public GuiStyle getStyle() {
      return this.style;
   }

   public void setBuffXY(BuffStyle buffStyle, int x, int y) {
      this.buffStyle = buffStyle;
      this.buffX = x;
      this.buffY = y;
      this.dirty = true;
   }

   public BuffStyle getBuffStyle() {
      return this.buffStyle;
   }

   public int getBuffX() {
      return this.buffX;
   }

   public int getBuffY() {
      return this.buffY;
   }
}
