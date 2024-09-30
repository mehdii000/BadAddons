package mehdi.bad.addons.Commands.Subcommands;

import mehdi.bad.addons.BadAddons;
import mehdi.bad.addons.utils.ChatLib;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;

public class CreatepearlSubcommand implements Subcommand {

	private BlockPos startPos = null;
	private BlockPos endPos = null;
	public static BlockPos lookAtPosition = null;

	@Override
	public String getCommandName() {
		return "createpearl";
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public String getCommandUsage() {
		return "";
	}

	@Override
	public String getCommandDescription() {
		return "Sets positions and predicts Ender Pearl landing.";
	}

	@Override
	public boolean processCommand(ICommandSender sender, String[] args) {
		EntityPlayerSP player = BadAddons.mc.thePlayer;
		BlockPos currentPos = player.getPosition();

		if (startPos == null) {
			startPos = currentPos;
			ChatLib.chat("§e[BA] §bSet starting position: " + currentPos);
		} else if (endPos == null) {
			endPos = currentPos;
			ChatLib.chat("§e[BA] §cSet ending position: " + currentPos);

			// Now that we have both start and end positions, calculate the aiming point
			lookAtPosition = calculateAimingPoint(startPos, endPos);

			// Predict and display where the Ender Pearl would land
			BlockPos predictedLanding = predictEnderPearlLanding(player, 1.5F); // Adjust velocity as needed
			renderPredictedLanding(player, predictedLanding);

		} else {
			player.addChatMessage(new ChatComponentText("§6[BA] §7Positions already set. Resetting."));
			startPos = null;
			endPos = null;
		}
		return true;
	}

	// Method to predict the Ender Pearl landing position
	private BlockPos predictEnderPearlLanding(EntityPlayerSP player, float velocity) {
		Vec3 lookVec = player.getLookVec(); // Get the direction the player is looking at

		double pearlPosX = player.posX;
		double pearlPosY = player.posY + player.getEyeHeight();
		double pearlPosZ = player.posZ;

		double motionX = lookVec.xCoord * velocity;
		double motionY = lookVec.yCoord * velocity;
		double motionZ = lookVec.zCoord * velocity;

		for (int i = 0; i < 100; i++) {  // Simulate for 100 ticks
			pearlPosX += motionX;
			pearlPosY += motionY;
			pearlPosZ += motionZ;

			motionY -= 0.03D; // Ender Pearl gravity (adjust as needed)

			motionX *= 0.99;
			motionY *= 0.99;
			motionZ *= 0.99;

			BlockPos predictedPos = new BlockPos(pearlPosX, pearlPosY, pearlPosZ);

			if (player.worldObj.getBlockState(predictedPos).getBlock().isAir(player.worldObj, predictedPos)) {
				return predictedPos; // Ender Pearl will land here
			}
		}

		return new BlockPos(pearlPosX, pearlPosY, pearlPosZ); // Final position after simulation
	}

	// Method to render particles at the predicted landing position
	private void renderPredictedLanding(EntityPlayerSP player, BlockPos predictedPos) {
		// Example: Render particles at the predicted location
		player.worldObj.spawnParticle(EnumParticleTypes.FLAME,
				predictedPos.getX() + 0.5, predictedPos.getY() + 1, predictedPos.getZ() + 0.5,
				0, 0, 0);
	}

	// Example calculation of aiming point (this could be your own logic)
	private BlockPos calculateAimingPoint(BlockPos startPos, BlockPos endPos) {
		double midX = (startPos.getX() + endPos.getX()) / 2.0;
		double midY = (startPos.getY() + endPos.getY()) / 2.0;
		double midZ = (startPos.getZ() + endPos.getZ()) / 2.0;
		return new BlockPos(midX, midY, midZ);
	}
}