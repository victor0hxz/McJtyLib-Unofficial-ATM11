package mcjty.lib.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Networking {
   public static void registerMessages(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("mcjtylib").versioned("1.0").optional();
      registrar.playBidirectional(PacketAttachmentData.TYPE, PacketAttachmentData.CODEC, PacketAttachmentData::handle);
      registrar.playToServer(PacketSetGuiStyle.TYPE, PacketSetGuiStyle.CODEC, PacketSetGuiStyle::handle);
      registrar.playToServer(PacketOpenManual.TYPE, PacketOpenManual.CODEC, PacketOpenManual::handle);
      registrar.playToServer(PacketGetListFromServer.TYPE, PacketGetListFromServer.CODEC, PacketGetListFromServer::handle);
      registrar.playToServer(PacketServerCommandTyped.TYPE, PacketServerCommandTyped.CODEC, PacketServerCommandTyped::handle);
      registrar.playToServer(PacketSendServerCommand.TYPE, PacketSendServerCommand.CODEC, PacketSendServerCommand::handle);
      registrar.playToServer(PacketRequestDataFromServer.TYPE, PacketRequestDataFromServer.CODEC, PacketRequestDataFromServer::handle);
      registrar.playToClient(PacketSendPreferencesToClient.TYPE, PacketSendPreferencesToClient.CODEC);
      registrar.playToClient(PacketContainerDataToClient.TYPE, PacketContainerDataToClient.CODEC);
      registrar.playToClient(PacketSendResultToClient.TYPE, PacketSendResultToClient.CODEC);
      registrar.playToClient(PacketSendClientCommand.TYPE, PacketSendClientCommand.CODEC);
      registrar.playToClient(PacketDataFromServer.TYPE, PacketDataFromServer.CODEC);
      registrar.playToClient(PacketFinalizeLogin.TYPE, PacketFinalizeLogin.CODEC);
   }

   public static void registerClientMessages(RegisterClientPayloadHandlersEvent event) {
      event.register(PacketAttachmentData.TYPE, PacketAttachmentData::handle);
      event.register(PacketSendPreferencesToClient.TYPE, PacketSendPreferencesToClient::handle);
      event.register(PacketContainerDataToClient.TYPE, PacketContainerDataToClient::handle);
      event.register(PacketSendResultToClient.TYPE, PacketSendResultToClient::handle);
      event.register(PacketSendClientCommand.TYPE, PacketSendClientCommand::handle);
      event.register(PacketDataFromServer.TYPE, PacketDataFromServer::handle);
      event.register(PacketFinalizeLogin.TYPE, PacketFinalizeLogin::handle);
   }

   public static void sendToServer(CustomPacketPayload msg) {
      ClientPacketDistributor.sendToServer(msg, new CustomPacketPayload[0]);
   }

   public static <T extends CustomPacketPayload> void sendToPlayer(T packet, Player player) {
      PacketDistributor.sendToPlayer((ServerPlayer)player, packet, new CustomPacketPayload[0]);
   }
}
