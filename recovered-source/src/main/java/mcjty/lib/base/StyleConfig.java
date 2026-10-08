package mcjty.lib.base;

import java.util.HashMap;
import java.util.Map;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

public class StyleConfig {
   public static final String CATEGORY_STYLE = "style";
   public static int colorSliderTopLeft = -13948117;
   public static int colorSliderBottomRight = -1;
   public static int colorSliderFiller = -10263709;
   public static int colorSliderKnobTopLeft = -1118482;
   public static int colorSliderKnobBottomRight = -13421773;
   public static int colorSliderKnobFiller = -7631989;
   public static int colorSliderKnobDraggingTopLeft = -10721635;
   public static int colorSliderKnobDraggingBottomRight = -4405761;
   public static int colorSliderKnobDraggingFiller = -8418881;
   public static int colorSliderKnobMarkerLine = -11645362;
   public static int colorSliderKnobHoveringTopLeft = -5920059;
   public static int colorSliderKnobHoveringBottomRight = -8946535;
   public static int colorSliderKnobHoveringFiller = -8025435;
   public static int colorTextNormal = -13619152;
   public static int colorTextInListNormal = -15395563;
   public static int colorTextDisabled = -6250336;
   public static int colorTextFieldFiller = -3750202;
   public static int colorTextFieldFocusedFiller = -1118482;
   public static int colorTextFieldHoveringFiller = -2434342;
   public static int colorTextFieldCursor = -16777216;
   public static int colorTextFieldTopLeft = -13948117;
   public static int colorTextFieldBottomRight = -1;
   public static int colorEnergyBarTopLeft = -13948117;
   public static int colorEnergyBarBottomRight = -1;
   public static int colorEnergyBarHighEnergy = -2293760;
   public static int colorEnergyBarLowEnergy = -10284783;
   public static int colorEnergyBarSpacer = -12386304;
   public static int colorEnergyBarText = -1;
   public static int colorListBackground = -7631989;
   public static int colorListSeparatorLine = -10724260;
   public static int colorListSelectedHighlightedGradient1 = -4474112;
   public static int colorListSelectedHighlightedGradient2 = -6711040;
   public static int colorListSelectedGradient1 = -10395295;
   public static int colorListSelectedGradient2 = -12500671;
   public static int colorListHighlightedGradient1 = -9342688;
   public static int colorListHighlightedGradient2 = -11448048;
   public static int colorBackgroundBevelBright = -1;
   public static int colorBackgroundBevelDark = -13948117;
   public static int colorBackgroundFiller = -3750202;
   public static int colorToggleNormalBorderTopLeft = -1118482;
   public static int colorToggleNormalBorderBottomRight = -8947849;
   public static int colorToggleNormalFiller = -3750202;
   public static int colorToggleDisabledBorderTopLeft = -1118482;
   public static int colorToggleDisabledBorderBottomRight = -8947849;
   public static int colorToggleDisabledFiller = -3750202;
   public static int colorToggleTextNormal = -13619152;
   public static int colorToggleTextDisabled = -6250336;
   public static int colorButtonExternalBorder = -16777216;
   public static int colorCycleButtonTriangleNormal = -16777216;
   public static int colorCycleButtonTriangleDisabled = -7829368;
   public static int colorButtonBorderTopLeft = -1118482;
   public static int colorButtonBorderBottomRight = -8947849;
   public static int colorButtonFiller = -3750202;
   public static int colorButtonFillerGradient1 = -5131855;
   public static int colorButtonFillerGradient2 = -1973791;
   public static int colorButtonDisabledBorderTopLeft = -1118482;
   public static int colorButtonDisabledBorderBottomRight = -8947849;
   public static int colorButtonDisabledFiller = -3750202;
   public static int colorButtonDisabledFillerGradient1 = -5131855;
   public static int colorButtonDisabledFillerGradient2 = -1973791;
   public static int colorButtonSelectedBorderTopLeft = -10721635;
   public static int colorButtonSelectedBorderBottomRight = -4405761;
   public static int colorButtonSelectedFiller = -8418881;
   public static int colorButtonSelectedFillerGradient1 = -9800534;
   public static int colorButtonSelectedFillerGradient2 = -7037228;
   public static int colorButtonHoveringBorderTopLeft = -5920059;
   public static int colorButtonHoveringBorderBottomRight = -6709573;
   public static int colorButtonHoveringFiller = -6117438;
   public static int colorButtonHoveringFillerGradient1 = -7499091;
   public static int colorButtonHoveringFillerGradient2 = -4538406;
   private static Map<String, ConfigValue<String>> colorConfigValues = new HashMap<>();

   public static void init(Builder CLIENT_BUILDER) {
      CLIENT_BUILDER.comment("Style settings for all mods using mcjtylib").push("style");
      initSetting(CLIENT_BUILDER, "colorSliderTopLeft", colorSliderTopLeft, "Color: slider top left border");
      initSetting(CLIENT_BUILDER, "colorSliderBottomRight", colorSliderBottomRight, "Color: slider bottom right border");
      initSetting(CLIENT_BUILDER, "colorSliderFiller", colorSliderFiller, "Color: slider background");
      initSetting(CLIENT_BUILDER, "colorSliderKnobTopLeft", colorSliderKnobTopLeft, "Color: slider knob top left border");
      initSetting(CLIENT_BUILDER, "colorSliderKnobBottomRight", colorSliderKnobBottomRight, "Color: slider knob bottom right border");
      initSetting(CLIENT_BUILDER, "colorSliderKnobFiller", colorSliderKnobFiller, "Color: slider knob background");
      initSetting(CLIENT_BUILDER, "colorSliderKnobDraggingTopLeft", colorSliderKnobDraggingTopLeft, "Color: slider knob top left border while dragging");
      initSetting(
         CLIENT_BUILDER, "colorSliderKnobDraggingBottomRight", colorSliderKnobDraggingBottomRight, "Color: slider knob bottom right border while dragging"
      );
      initSetting(CLIENT_BUILDER, "colorSliderKnobDraggingFiller", colorSliderKnobDraggingFiller, "Color: slider knob background while dragging");
      initSetting(CLIENT_BUILDER, "colorSliderKnobHoveringTopLeft", colorSliderKnobHoveringTopLeft, "Color: slider knob top left border while hovering");
      initSetting(
         CLIENT_BUILDER, "colorSliderKnobHoveringBottomRight", colorSliderKnobHoveringBottomRight, "Color: slider knob bottom right border while hovering"
      );
      initSetting(CLIENT_BUILDER, "colorSliderKnobHoveringFiller", colorSliderKnobHoveringFiller, "Color: slider knob background while hovering");
      initSetting(CLIENT_BUILDER, "colorSliderKnobMarkerLine", colorSliderKnobMarkerLine, "Color: slider knob little marker lines");
      initSetting(CLIENT_BUILDER, "colorTextNormal", colorTextNormal, "Color: text normal");
      initSetting(CLIENT_BUILDER, "colorTextInListNormal", colorTextInListNormal, "Color: text as used in lists");
      initSetting(CLIENT_BUILDER, "colorTextDisabled", colorTextDisabled, "Color: text disabled");
      initSetting(CLIENT_BUILDER, "colorTextFieldTopLeft", colorTextFieldTopLeft, "Color: textfield top left border");
      initSetting(CLIENT_BUILDER, "colorTextFieldBottomRight", colorTextFieldBottomRight, "Color: textfield bottom right border");
      initSetting(CLIENT_BUILDER, "colorTextFieldFiller", colorTextFieldFiller, "Color: textfield background");
      initSetting(CLIENT_BUILDER, "colorTextFieldFocusedFiller", colorTextFieldFocusedFiller, "Color: textfield backbground while focused");
      initSetting(CLIENT_BUILDER, "colorTextFieldHoveringFiller", colorTextFieldHoveringFiller, "Color: textfield backbground while hovering");
      initSetting(CLIENT_BUILDER, "colorTextFieldCursor", colorTextFieldCursor, "Color: textfield cursor");
      initSetting(CLIENT_BUILDER, "colorEnergyBarTopLeft", colorEnergyBarTopLeft, "Color: energy bar top left border");
      initSetting(CLIENT_BUILDER, "colorEnergyBarBottomRight", colorEnergyBarBottomRight, "Color: energy bar bottom right border");
      initSetting(CLIENT_BUILDER, "colorEnergyBarHighEnergy", colorEnergyBarHighEnergy, "Color: energy bar high energy level");
      initSetting(CLIENT_BUILDER, "colorEnergyBarLowEnergy", colorEnergyBarLowEnergy, "Color: energy bar low energy level");
      initSetting(CLIENT_BUILDER, "colorEnergyBarSpacer", colorEnergyBarSpacer, "Color: energy bar spacer (between every energy level bar)");
      initSetting(CLIENT_BUILDER, "colorEnergyBarText", colorEnergyBarText, "Color: energy bar text");
      initSetting(CLIENT_BUILDER, "colorListBackground", colorListBackground, "Color: list background");
      initSetting(CLIENT_BUILDER, "colorListSeparatorLine", colorListSeparatorLine, "Color: list separator line");
      initSetting(
         CLIENT_BUILDER, "colorListSelectedHighlightedGradient1", colorListSelectedHighlightedGradient1, "Color: list selected and highlighted gradient"
      );
      initSetting(
         CLIENT_BUILDER, "colorListSelectedHighlightedGradient2", colorListSelectedHighlightedGradient2, "Color: list selected and highlighted gradient"
      );
      initSetting(CLIENT_BUILDER, "colorListSelectedGradient1", colorListSelectedGradient1, "Color: list selected gradient");
      initSetting(CLIENT_BUILDER, "colorListSelectedGradient2", colorListSelectedGradient2, "Color: list selected gradient");
      initSetting(CLIENT_BUILDER, "colorListHighlightedGradient1", colorListHighlightedGradient1, "Color: list highlighted gradient");
      initSetting(CLIENT_BUILDER, "colorListHighlightedGradient2", colorListHighlightedGradient2, "Color: list highlighted gradient");
      initSetting(CLIENT_BUILDER, "colorBackgroundBevelBright", colorBackgroundBevelBright, "Color: standard bevel bright border color");
      initSetting(CLIENT_BUILDER, "colorBackgroundBevelDark", colorBackgroundBevelDark, "Color: standard bevel dark border color");
      initSetting(CLIENT_BUILDER, "colorBackgroundFiller", colorBackgroundFiller, "Color: standard background color");
      initSetting(CLIENT_BUILDER, "colorToggleNormalBorderTopLeft", colorToggleNormalBorderTopLeft, "Color: toggle button normal top left border");
      initSetting(CLIENT_BUILDER, "colorToggleNormalBorderBottomRight", colorToggleNormalBorderBottomRight, "Color: toggle button normal bottom right border");
      initSetting(CLIENT_BUILDER, "colorToggleNormalFiller", colorToggleNormalFiller, "Color: toggle button normal background");
      initSetting(CLIENT_BUILDER, "colorToggleDisabledBorderTopLeft", colorToggleDisabledBorderTopLeft, "Color: toggle button disabled top left border");
      initSetting(
         CLIENT_BUILDER, "colorToggleDisabledBorderBottomRight", colorToggleDisabledBorderBottomRight, "Color: toggle button disabled bottom right border"
      );
      initSetting(CLIENT_BUILDER, "colorToggleDisabledFiller", colorToggleDisabledFiller, "Color: toggle button disabled background");
      initSetting(CLIENT_BUILDER, "colorToggleTextNormal", colorToggleTextNormal, "Color: toggle button normal text");
      initSetting(CLIENT_BUILDER, "colorToggleTextDisabled", colorToggleTextDisabled, "Color: toggle button disabled text");
      initSetting(CLIENT_BUILDER, "colorCycleButtonTriangleNormal", colorCycleButtonTriangleNormal, "Color: cycle button small triangle");
      initSetting(CLIENT_BUILDER, "colorCycleButtonTriangleDisabled", colorCycleButtonTriangleDisabled, "Color: cycle button disabled small triangle");
      initSetting(CLIENT_BUILDER, "colorButtonExternalBorder", colorButtonExternalBorder, "Color: external border around buttons and some other components");
      initSetting(CLIENT_BUILDER, "colorButtonBorderTopLeft", colorButtonBorderTopLeft, "Color: button top left border");
      initSetting(CLIENT_BUILDER, "colorButtonBorderBottomRight", colorButtonBorderBottomRight, "Color: button bottom right border");
      initSetting(CLIENT_BUILDER, "colorButtonFiller", colorButtonFiller, "Color: button background");
      initSetting(CLIENT_BUILDER, "colorButtonFillerGradient1", colorButtonFillerGradient1, "Color: button background gradient");
      initSetting(CLIENT_BUILDER, "colorButtonFillerGradient2", colorButtonFillerGradient2, "Color: button background gradient");
      initSetting(CLIENT_BUILDER, "colorButtonDisabledBorderTopLeft", colorButtonDisabledBorderTopLeft, "Color: disabled button top left border");
      initSetting(CLIENT_BUILDER, "colorButtonDisabledBorderBottomRight", colorButtonDisabledBorderBottomRight, "Color: disabled button bottom right border");
      initSetting(CLIENT_BUILDER, "colorButtonDisabledFiller", colorButtonDisabledFiller, "Color: disabled button background");
      initSetting(CLIENT_BUILDER, "colorButtonDisabledFillerGradient1", colorButtonDisabledFillerGradient1, "Color: disabled button background gradient");
      initSetting(CLIENT_BUILDER, "colorButtonDisabledFillerGradient2", colorButtonDisabledFillerGradient2, "Color: disabled button background gradient");
      initSetting(CLIENT_BUILDER, "colorButtonSelectedBorderTopLeft", colorButtonSelectedBorderTopLeft, "Color: selected button top left border");
      initSetting(CLIENT_BUILDER, "colorButtonSelectedBorderBottomRight", colorButtonSelectedBorderBottomRight, "Color: selected button bottom right border");
      initSetting(CLIENT_BUILDER, "colorButtonSelectedFiller", colorButtonSelectedFiller, "Color: selected button background");
      initSetting(CLIENT_BUILDER, "colorButtonSelectedFillerGradient1", colorButtonSelectedFillerGradient1, "Color: selected button background gradient");
      initSetting(CLIENT_BUILDER, "colorButtonSelectedFillerGradient2", colorButtonSelectedFillerGradient2, "Color: selected button background gradient");
      initSetting(CLIENT_BUILDER, "colorButtonHoveringBorderTopLeft", colorButtonHoveringBorderTopLeft, "Color: hovering button top left border");
      initSetting(CLIENT_BUILDER, "colorButtonHoveringBorderBottomRight", colorButtonHoveringBorderBottomRight, "Color: hovering button bottom right border");
      initSetting(CLIENT_BUILDER, "colorButtonHoveringFiller", colorButtonHoveringFiller, "Color: hovering button background");
      initSetting(CLIENT_BUILDER, "colorButtonHoveringFillerGradient1", colorButtonHoveringFillerGradient1, "Color: hovering button background gradient");
      initSetting(CLIENT_BUILDER, "colorButtonHoveringFillerGradient2", colorButtonHoveringFillerGradient2, "Color: hovering button background gradient");
      CLIENT_BUILDER.pop();
   }

   private static void initSetting(Builder CLIENT_BUILDER, String settingName, int setting, String comment) {
      ConfigValue<String> value = CLIENT_BUILDER.comment(comment).define(settingName, Integer.toHexString(setting & 16777215));
      colorConfigValues.put(settingName, value);
   }

   private static int resolveColor(String name) {
      return -16777216 + Integer.parseInt((String)colorConfigValues.get(name).get(), 16);
   }

   public static void updateColors() {
      colorSliderTopLeft = resolveColor("colorSliderTopLeft");
      colorSliderBottomRight = resolveColor("colorSliderBottomRight");
      colorSliderFiller = resolveColor("colorSliderFiller");
      colorSliderKnobTopLeft = resolveColor("colorSliderKnobTopLeft");
      colorSliderKnobBottomRight = resolveColor("colorSliderKnobBottomRight");
      colorSliderKnobFiller = resolveColor("colorSliderKnobFiller");
      colorSliderKnobDraggingTopLeft = resolveColor("colorSliderKnobDraggingTopLeft");
      colorSliderKnobDraggingBottomRight = resolveColor("colorSliderKnobDraggingBottomRight");
      colorSliderKnobDraggingFiller = resolveColor("colorSliderKnobDraggingFiller");
      colorSliderKnobHoveringTopLeft = resolveColor("colorSliderKnobHoveringTopLeft");
      colorSliderKnobHoveringBottomRight = resolveColor("colorSliderKnobHoveringBottomRight");
      colorSliderKnobHoveringFiller = resolveColor("colorSliderKnobHoveringFiller");
      colorSliderKnobMarkerLine = resolveColor("colorSliderKnobMarkerLine");
      colorTextNormal = resolveColor("colorTextNormal");
      colorTextInListNormal = resolveColor("colorTextInListNormal");
      colorTextDisabled = resolveColor("colorTextDisabled");
      colorTextFieldTopLeft = resolveColor("colorTextFieldTopLeft");
      colorTextFieldBottomRight = resolveColor("colorTextFieldBottomRight");
      colorTextFieldFiller = resolveColor("colorTextFieldFiller");
      colorTextFieldFocusedFiller = resolveColor("colorTextFieldFocusedFiller");
      colorTextFieldHoveringFiller = resolveColor("colorTextFieldHoveringFiller");
      colorTextFieldCursor = resolveColor("colorTextFieldCursor");
      colorEnergyBarTopLeft = resolveColor("colorEnergyBarTopLeft");
      colorEnergyBarBottomRight = resolveColor("colorEnergyBarBottomRight");
      colorEnergyBarHighEnergy = resolveColor("colorEnergyBarHighEnergy");
      colorEnergyBarLowEnergy = resolveColor("colorEnergyBarLowEnergy");
      colorEnergyBarSpacer = resolveColor("colorEnergyBarSpacer");
      colorEnergyBarText = resolveColor("colorEnergyBarText");
      colorListBackground = resolveColor("colorListBackground");
      colorListSeparatorLine = resolveColor("colorListSeparatorLine");
      colorListSelectedHighlightedGradient1 = resolveColor("colorListSelectedHighlightedGradient1");
      colorListSelectedHighlightedGradient2 = resolveColor("colorListSelectedHighlightedGradient2");
      colorListSelectedGradient1 = resolveColor("colorListSelectedGradient1");
      colorListSelectedGradient2 = resolveColor("colorListSelectedGradient2");
      colorListHighlightedGradient1 = resolveColor("colorListHighlightedGradient1");
      colorListHighlightedGradient2 = resolveColor("colorListHighlightedGradient2");
      colorBackgroundBevelBright = resolveColor("colorBackgroundBevelBright");
      colorBackgroundBevelDark = resolveColor("colorBackgroundBevelDark");
      colorBackgroundFiller = resolveColor("colorBackgroundFiller");
      colorToggleNormalBorderTopLeft = resolveColor("colorToggleNormalBorderTopLeft");
      colorToggleNormalBorderBottomRight = resolveColor("colorToggleNormalBorderBottomRight");
      colorToggleNormalFiller = resolveColor("colorToggleNormalFiller");
      colorToggleDisabledBorderTopLeft = resolveColor("colorToggleDisabledBorderTopLeft");
      colorToggleDisabledBorderBottomRight = resolveColor("colorToggleDisabledBorderBottomRight");
      colorToggleDisabledFiller = resolveColor("colorToggleDisabledFiller");
      colorToggleTextNormal = resolveColor("colorToggleTextNormal");
      colorToggleTextDisabled = resolveColor("colorToggleTextDisabled");
      colorCycleButtonTriangleNormal = resolveColor("colorCycleButtonTriangleNormal");
      colorCycleButtonTriangleDisabled = resolveColor("colorCycleButtonTriangleDisabled");
      colorButtonExternalBorder = resolveColor("colorButtonExternalBorder");
      colorButtonBorderTopLeft = resolveColor("colorButtonBorderTopLeft");
      colorButtonBorderBottomRight = resolveColor("colorButtonBorderBottomRight");
      colorButtonFiller = resolveColor("colorButtonFiller");
      colorButtonFillerGradient1 = resolveColor("colorButtonFillerGradient1");
      colorButtonFillerGradient2 = resolveColor("colorButtonFillerGradient2");
      colorButtonDisabledBorderTopLeft = resolveColor("colorButtonDisabledBorderTopLeft");
      colorButtonDisabledBorderBottomRight = resolveColor("colorButtonDisabledBorderBottomRight");
      colorButtonDisabledFiller = resolveColor("colorButtonDisabledFiller");
      colorButtonDisabledFillerGradient1 = resolveColor("colorButtonDisabledFillerGradient1");
      colorButtonDisabledFillerGradient2 = resolveColor("colorButtonDisabledFillerGradient2");
      colorButtonSelectedBorderTopLeft = resolveColor("colorButtonSelectedBorderTopLeft");
      colorButtonSelectedBorderBottomRight = resolveColor("colorButtonSelectedBorderBottomRight");
      colorButtonSelectedFiller = resolveColor("colorButtonSelectedFiller");
      colorButtonSelectedFillerGradient1 = resolveColor("colorButtonSelectedFillerGradient1");
      colorButtonSelectedFillerGradient2 = resolveColor("colorButtonSelectedFillerGradient2");
      colorButtonHoveringBorderTopLeft = resolveColor("colorButtonHoveringBorderTopLeft");
      colorButtonHoveringBorderBottomRight = resolveColor("colorButtonHoveringBorderBottomRight");
      colorButtonHoveringFiller = resolveColor("colorButtonHoveringFiller");
      colorButtonHoveringFillerGradient1 = resolveColor("colorButtonHoveringFillerGradient1");
      colorButtonHoveringFillerGradient2 = resolveColor("colorButtonHoveringFillerGradient2");
   }
}
