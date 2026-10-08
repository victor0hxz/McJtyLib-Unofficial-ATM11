package mcjty.lib.gui;

public interface IKeyReceiver {
   Window getWindow();

   void keyTypedFromEvent(int var1, int var2);

   boolean mouseClickedFromEvent(double var1, double var3, int var5);

   boolean mouseReleasedFromEvent(double var1, double var3, int var5);

   boolean mouseScrolledFromEvent(double var1, double var3, double var5, double var7);

   void charTypedFromEvent(char var1);
}
