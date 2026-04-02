package com.github.theredbrain.mobbrainadditions.gui.screen.ingame;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingBranchingNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingBranchingNodeBlockPacket;
import com.github.theredbrain.mobbrainadditions.util.ParsingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.MutablePair;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Environment(value = EnvType.CLIENT)
public class PathFindingBranchingNodeBlockScreen extends Screen {
	private static final Text NODE_IDENTIFIER_LABEL_TEXT = Text.translatable("gui.path_finding_branching_node_block.node_identifier");
	private static final Text NEW_BRANCH_POSITION_OFFSET_LABEL_TEXT = Text.translatable("gui.path_finding_branching_node_block.new_branch_position_offset");
	private static final Text NEW_BRANCH_WEIGHT_LABEL_TEXT = Text.translatable("gui.path_finding_branching_node_block.new_branch_weight");
	private static final Text ADD_NEW_BRANCH_BUTTON_LABEL_TEXT = Text.translatable("gui.path_finding_branching_node_block.add_new_branch_button_label");
	private static final Identifier SCROLL_BAR_BACKGROUND_8_92_TEXTURE = MobBrainAdditions.identifier("scroll_bar/scroll_bar_background_8_92");
	private static final Identifier SCROLLER_TEXTURE = MobBrainAdditions.identifier("scroll_bar/scroller_vertical_6_7");
	public static final ButtonTextures REMOVE_ENTRY_BUTTON_TEXTURES = new ButtonTextures(
			MobBrainAdditions.identifier("widgets/remove_entry_button"), MobBrainAdditions.identifier("widgets/remove_entry_button_highlighted")
	);
	private final PathFindingBranchingNodeBlockEntity pathFindingBranchingNodeBlockEntity;
	private TextFieldWidget nodeIdentifierField;
	private ButtonWidget removeBranchButton0;
	private ButtonWidget removeBranchButton1;
	private ButtonWidget removeBranchButton2;
	private ButtonWidget removeBranchButton3;
	private TextFieldWidget newBranchWeightField;
	private TextFieldWidget newBranchPositionOffsetXField;
	private TextFieldWidget newBranchPositionOffsetYField;
	private TextFieldWidget newBranchPositionOffsetZField;
	private ButtonWidget addNewBranchButton;

	private ButtonWidget saveButton;
	private ButtonWidget cancelButton;

	private final List<MutablePair<Integer, BlockPos>> branchList = new ArrayList<>();

	private int scrollPosition = 0;
	private float scrollAmount = 0.0f;
	private boolean mouseClicked = false;

	public PathFindingBranchingNodeBlockScreen(PathFindingBranchingNodeBlockEntity pathFindingBranchingNodeBlockEntity) {
		super(NarratorManager.EMPTY);
		this.pathFindingBranchingNodeBlockEntity = pathFindingBranchingNodeBlockEntity;
	}

	private void addNewBranch() {
		String newWeightString = this.newBranchWeightField.getText();
		this.branchList.add(new MutablePair<>(
				newWeightString.isEmpty() ? 1 : ParsingUtils.parseInt(newWeightString),
						new BlockPos(
								ParsingUtils.parseInt(this.newBranchPositionOffsetXField.getText()),
								ParsingUtils.parseInt(this.newBranchPositionOffsetYField.getText()),
								ParsingUtils.parseInt(this.newBranchPositionOffsetZField.getText())
						)
				)
		);
		this.scrollPosition = 0;
		this.scrollAmount = 0.0f;
		this.updateWidgets();
	}

	private void removeBranch(int index) {
		int actualIndex = index + this.scrollPosition;
		if (this.branchList.size() > actualIndex) {
			this.branchList.remove(actualIndex);
		}
		this.scrollPosition = 0;
		this.scrollAmount = 0.0f;
		this.updateWidgets();
	}

	private void done() {
		if (this.updatePathFindingNodeBlock()) {
			this.close();
		}
	}

	private void cancel() {
		this.close();
	}

	@Override
	protected void init() {
		this.branchList.clear();
		this.branchList.addAll(this.pathFindingBranchingNodeBlockEntity.getBranches());

		super.init();

		this.nodeIdentifierField = new TextFieldWidget(this.textRenderer, this.width / 2 - 154, 30, 300, 20, Text.empty());
		this.nodeIdentifierField.setMaxLength(128);
		this.nodeIdentifierField.setText(this.pathFindingBranchingNodeBlockEntity.getNodeId());
		this.addSelectableChild(this.nodeIdentifierField);

		this.removeBranchButton0 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 54, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeBranch(0)));
		this.removeBranchButton1 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 78, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeBranch(1)));
		this.removeBranchButton2 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 102, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeBranch(2)));
		this.removeBranchButton3 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 126, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeBranch(3)));

		this.addNewBranchButton = this.addDrawableChild(ButtonWidget.builder(ADD_NEW_BRANCH_BUTTON_LABEL_TEXT, button -> this.addNewBranch()).dimensions(this.width / 2 - 4 - 150, 150, 308, 20).build());

		this.newBranchPositionOffsetXField = new TextFieldWidget(this.textRenderer, this.width / 2 - 154, 185, 75, 20, Text.empty());
		this.newBranchPositionOffsetXField.setMaxLength(128);
		this.addSelectableChild(this.newBranchPositionOffsetXField);

		this.newBranchPositionOffsetYField = new TextFieldWidget(this.textRenderer, this.width / 2 - 75, 185, 75, 20, Text.empty());
		this.newBranchPositionOffsetYField.setMaxLength(128);
		this.addSelectableChild(this.newBranchPositionOffsetYField);

		this.newBranchPositionOffsetZField = new TextFieldWidget(this.textRenderer, this.width / 2 + 4, 185, 75, 20, Text.empty());
		this.newBranchPositionOffsetZField.setMaxLength(128);
		this.addSelectableChild(this.newBranchPositionOffsetZField);

		this.newBranchWeightField = new TextFieldWidget(this.textRenderer, this.width / 2 + 83, 185, 75, 20, Text.empty());
		this.newBranchWeightField.setMaxLength(128);
		this.newBranchWeightField.setPlaceholder(Text.literal("1"));
		this.addSelectableChild(this.newBranchWeightField);

		this.saveButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.done()).dimensions(this.width / 2 - 4 - 150, 210, 150, 20).build());
		this.cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.cancel()).dimensions(this.width / 2 + 4, 210, 150, 20).build());

		this.updateWidgets();
	}

	private void updateWidgets() {

		this.nodeIdentifierField.setVisible(false);

		this.removeBranchButton0.visible = false;
		this.removeBranchButton1.visible = false;
		this.removeBranchButton2.visible = false;
		this.removeBranchButton3.visible = false;

		this.newBranchWeightField.setVisible(false);
		this.newBranchPositionOffsetXField.setVisible(false);
		this.newBranchPositionOffsetYField.setVisible(false);
		this.newBranchPositionOffsetZField.setVisible(false);
		this.addNewBranchButton.visible = false;

		this.saveButton.visible = false;
		this.cancelButton.visible = false;

		this.nodeIdentifierField.setVisible(true);

		int index = 0;
		for (int i = 0; i < Math.min(4, this.branchList.size()); i++) {
			if (index == 0) {
				this.removeBranchButton0.visible = true;
			} else if (index == 1) {
				this.removeBranchButton1.visible = true;
			} else if (index == 2) {
				this.removeBranchButton2.visible = true;
			} else if (index == 3) {
				this.removeBranchButton3.visible = true;
			}
			index++;
		}

		this.newBranchPositionOffsetXField.setVisible(true);
		this.newBranchPositionOffsetYField.setVisible(true);
		this.newBranchPositionOffsetZField.setVisible(true);
		this.newBranchWeightField.setVisible(true);
		this.addNewBranchButton.visible = true;

		this.saveButton.visible = true;
		this.cancelButton.visible = true;

	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.nodeIdentifierField);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderInGameBackground(context);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		List<MutablePair<Integer, BlockPos>> list = new ArrayList<>(this.branchList);
		int number = this.scrollPosition;
		float number1 = this.scrollAmount;
		String string = this.newBranchWeightField.getText();
		String string1 = this.newBranchPositionOffsetXField.getText();
		String string2 = this.newBranchPositionOffsetYField.getText();
		String string3 = this.newBranchPositionOffsetZField.getText();
		String string4 = this.nodeIdentifierField.getText();
		this.init(client, width, height);
		this.branchList.clear();
		this.branchList.addAll(list);
		this.scrollPosition = number;
		this.scrollAmount = number1;
		this.newBranchWeightField.setText(string);
		this.newBranchPositionOffsetXField.setText(string1);
		this.newBranchPositionOffsetYField.setText(string2);
		this.newBranchPositionOffsetZField.setText(string3);
		this.nodeIdentifierField.setText(string4);
		this.updateWidgets();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.mouseClicked = false;
		if (this.branchList.size() > 4) {
			int i = this.width / 2 - 152;
			int j = 55;
			if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 90)) {
				this.mouseClicked = true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.branchList.size() > 4
				&& this.mouseClicked) {
			int i = this.branchList.size() - 4;
			float f = (float) deltaY / (float) i;
			this.scrollAmount = MathHelper.clamp(this.scrollAmount + f, 0.0f, 1.0f);
			this.scrollPosition = (int) ((double) (this.scrollAmount * (float) i));
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (this.branchList.size() > 4
				&& mouseX >= (double) (this.width / 2 - 152) && mouseX <= (double) (this.width / 2 + 154)
				&& mouseY >= 54 && mouseY <= 146) {
			int i = this.branchList.size() - 4;
			float f = (float) verticalAmount / (float) i;
			this.scrollAmount = MathHelper.clamp(this.scrollAmount - f, 0.0f, 1.0f);
			this.scrollPosition = (int) ((double) (this.scrollAmount * (float) i));
		}
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if ((keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER)) {
			this.done();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {

		super.render(context, mouseX, mouseY, delta);

		context.drawTextWithShadow(this.textRenderer, NODE_IDENTIFIER_LABEL_TEXT, this.width / 2 - 153, 20, 0xA0A0A0);
		this.nodeIdentifierField.render(context, mouseX, mouseY, delta);

		for (int i = this.scrollPosition; i < Math.min(this.scrollPosition + 4, this.branchList.size()); i++) {
			MutablePair<Integer, BlockPos> branch = this.branchList.get(i);
			BlockPos branchOffset = branch.getRight();
			context.drawTextWithShadow(this.textRenderer,
					Text.translatable("gui.path_finding_branching_node_block.branch_list.offset", branchOffset.getX(), branchOffset.getY(), branchOffset.getZ()),
					this.width / 2 - 117, 55 + ((i - this.scrollPosition) * 24), 0xA0A0A0);
			context.drawTextWithShadow(this.textRenderer,
					Text.translatable("gui.path_finding_branching_node_block.branch_list.weight", branch.getLeft()),
					this.width / 2 - 117, 65 + ((i - this.scrollPosition) * 24), 0xA0A0A0);
		}
		if (this.branchList.size() > 4) {
			context.drawGuiTexture(SCROLL_BAR_BACKGROUND_8_92_TEXTURE, this.width / 2 - 153, 54, 8, 92);
			int k = (int) (83.0f * this.scrollAmount);
			context.drawGuiTexture(SCROLLER_TEXTURE, this.width / 2 - 152, 54 + 1 + k, 6, 7);
		}
		int textWidth = textRenderer.getWidth(NEW_BRANCH_POSITION_OFFSET_LABEL_TEXT);
		context.drawTextWithShadow(this.textRenderer, NEW_BRANCH_POSITION_OFFSET_LABEL_TEXT, (this.width - textWidth) / 2 - 35, 175, 0xA0A0A0);
		this.newBranchPositionOffsetXField.render(context, mouseX, mouseY, delta);
		this.newBranchPositionOffsetYField.render(context, mouseX, mouseY, delta);
		this.newBranchPositionOffsetZField.render(context, mouseX, mouseY, delta);
		context.drawTextWithShadow(this.textRenderer, NEW_BRANCH_WEIGHT_LABEL_TEXT, this.width / 2 + 84, 175, 0xA0A0A0);
		this.newBranchWeightField.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	private boolean updatePathFindingNodeBlock() {
		ClientPlayNetworking.send(new UpdatePathFindingBranchingNodeBlockPacket(
				this.pathFindingBranchingNodeBlockEntity.getPos(),
				this.nodeIdentifierField.getText(),
				this.branchList
		));
		return true;
	}

}
