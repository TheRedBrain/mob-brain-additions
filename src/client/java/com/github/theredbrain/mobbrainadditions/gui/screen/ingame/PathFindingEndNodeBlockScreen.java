package com.github.theredbrain.mobbrainadditions.gui.screen.ingame;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingEndNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingEndNodeBlockPacket;
import com.github.theredbrain.mobbrainadditions.util.ParsingUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
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
import java.util.Map;

@Environment(value = EnvType.CLIENT)
public class PathFindingEndNodeBlockScreen extends Screen {
	private static final Text SCRIPT_BLOCKS_LABEL_TEXT = Text.translatable("gui.path_finding_end_node_block.script_blocks_mode_label");
	private static final Text RED_STONE_MODE_LABEL_TEXT = Text.translatable("gui.path_finding_end_node_block.red_stone_mode_label");
	private static final Text ADD_NEW_ENTRY_BUTTON_LABEL_TEXT = Text.translatable("gui.path_finding_end_node_block.add_new_entry_button_label");
	private static final Text NEW_IDENTIFIER_PLACEHOLDER_TEXT = Text.translatable("gui.path_finding_end_node_block.new_identifier");
	private static final Text NEW_BLOCK_OFFSET_LABEL_TEXT = Text.translatable("gui.path_finding_end_node_block.new_block_offset_label");
	private static final Identifier SCROLL_BAR_BACKGROUND_8_92_TEXTURE = MobBrainAdditions.identifier("scroll_bar/scroll_bar_background_8_92");
	private static final Identifier SCROLLER_TEXTURE = MobBrainAdditions.identifier("scroll_bar/scroller_vertical_6_7");
	public static final ButtonTextures REMOVE_ENTRY_BUTTON_TEXTURES = new ButtonTextures(
			MobBrainAdditions.identifier("widgets/remove_entry_button"), MobBrainAdditions.identifier("widgets/remove_entry_button_highlighted")
	);
	private final PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity;
	private ButtonWidget cycleModeBackwardsButton;
	private ButtonWidget cycleModeForwardsButton;
	private ButtonWidget removeTriggeredBlockButton0;
	private ButtonWidget removeTriggeredBlockButton1;
	private ButtonWidget removeTriggeredBlockButton2;
	private ButtonWidget removeTriggeredBlockButton3;
	private ButtonWidget addNewTriggeredBlockButton;
	private TextFieldWidget newTriggeredBlockIdentifierField;
	private TextFieldWidget newTriggeredBlockOffsetXField;
	private TextFieldWidget newTriggeredBlockOffsetYField;
	private TextFieldWidget newTriggeredBlockOffsetZField;
	private CyclingButtonWidget<Boolean> toggleNewTriggeredBlockResetsButton;

	private ButtonWidget saveButton;
	private ButtonWidget cancelButton;

	private final List<MutablePair<String, MutablePair<BlockPos, Boolean>>> triggeredBlocksList = new ArrayList<>();

	private boolean useScriptBlocksMode;
	private boolean newTriggeredBlockResets = false;

	private int scrollPosition = 0;
	private float scrollAmount = 0.0f;
	private boolean mouseClicked = false;

	public PathFindingEndNodeBlockScreen(PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity) {
		super(NarratorManager.EMPTY);
		this.pathFindingEndNodeBlockEntity = pathFindingEndNodeBlockEntity;
	}

	private void addNewNode() {
		String newEntranceName = this.newTriggeredBlockIdentifierField.getText();
		if (newEntranceName.isEmpty()) {
			return;
		}
		int indexToRemove = -1;
		for (int i = 0; i < this.triggeredBlocksList.size(); i++) {
			if (this.triggeredBlocksList.get(i).getLeft().equals(newEntranceName)) {
				indexToRemove = i;
				break;
			}
		}
		if (indexToRemove != -1) {
			this.triggeredBlocksList.remove(indexToRemove);
		}
		this.triggeredBlocksList.add(new MutablePair<>(
						this.newTriggeredBlockIdentifierField.getText(),
						this.useScriptBlocksMode ? new MutablePair<>(
								new BlockPos(
										ParsingUtils.parseInt(this.newTriggeredBlockOffsetXField.getText()),
										ParsingUtils.parseInt(this.newTriggeredBlockOffsetYField.getText()),
										ParsingUtils.parseInt(this.newTriggeredBlockOffsetZField.getText())
								), this.newTriggeredBlockResets
						) : new MutablePair<>(BlockPos.ORIGIN, false)
				)
		);
		this.scrollPosition = 0;
		this.scrollAmount = 0.0f;
		this.updateWidgets();
	}

	private void cycleMode() {
		this.useScriptBlocksMode = !this.useScriptBlocksMode;
		this.scrollPosition = 0;
		this.scrollAmount = 0.0f;
		this.updateWidgets();
	}

	private void removeNode(int index) {
		int actualIndex = index + this.scrollPosition;
		if (this.triggeredBlocksList.size() > actualIndex) {
			this.triggeredBlocksList.remove(actualIndex);
		}
		this.scrollPosition = 0;
		this.scrollAmount = 0.0f;
		this.updateWidgets();
	}

	private void done() {
		if (this.updatePathFindingEndNodeBlock()) {
			this.close();
		}
	}

	private void cancel() {
		this.close();
	}

	@Override
	protected void init() {
		this.triggeredBlocksList.clear();

		List<Map.Entry<String, MutablePair<BlockPos, Boolean>>> entryList = this.pathFindingEndNodeBlockEntity.getTriggeredBlocks().entrySet().stream().toList();
		for (Map.Entry<String, MutablePair<BlockPos, Boolean>> entry : entryList) {
			this.triggeredBlocksList.add(new MutablePair<>(entry.getKey(), entry.getValue()));
		}

		super.init();

		this.useScriptBlocksMode = this.pathFindingEndNodeBlockEntity.useScriptBlocksMode();
		this.cycleModeBackwardsButton = this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> this.cycleMode()).dimensions(this.width / 2 - 154, 20, 20, 20).build());
		this.cycleModeForwardsButton = this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> this.cycleMode()).dimensions(this.width / 2 + 134, 20, 20, 20).build());

		this.removeTriggeredBlockButton0 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 44, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(1)));
		this.removeTriggeredBlockButton1 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 68, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(2)));
		this.removeTriggeredBlockButton2 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 92, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(3)));
		this.removeTriggeredBlockButton3 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 116, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(4)));

		this.addNewTriggeredBlockButton = this.addDrawableChild(ButtonWidget.builder(ADD_NEW_ENTRY_BUTTON_LABEL_TEXT, button -> this.addNewNode()).dimensions(this.width / 2 - 4 - 150, 140, 308, 20).build());

		this.newTriggeredBlockIdentifierField = new TextFieldWidget(this.textRenderer, this.width / 2 - 154, 164, 300, 20, Text.empty());
		this.newTriggeredBlockIdentifierField.setMaxLength(128);
		this.newTriggeredBlockIdentifierField.setPlaceholder(NEW_IDENTIFIER_PLACEHOLDER_TEXT);
		this.addSelectableChild(this.newTriggeredBlockIdentifierField);

		this.newTriggeredBlockOffsetXField = new TextFieldWidget(this.textRenderer, this.width / 2 - 154, 199, 50, 20, Text.empty());
		this.newTriggeredBlockOffsetXField.setMaxLength(128);
		this.newTriggeredBlockOffsetXField.setPlaceholder(Text.literal("X"));
		this.addSelectableChild(this.newTriggeredBlockOffsetXField);

		this.newTriggeredBlockOffsetYField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 199, 50, 20, Text.empty());
		this.newTriggeredBlockOffsetYField.setMaxLength(128);
		this.newTriggeredBlockOffsetYField.setPlaceholder(Text.literal("Y"));
		this.addSelectableChild(this.newTriggeredBlockOffsetYField);

		this.newTriggeredBlockOffsetZField = new TextFieldWidget(this.textRenderer, this.width / 2 - 46, 199, 50, 20, Text.empty());
		this.newTriggeredBlockOffsetZField.setMaxLength(128);
		this.newTriggeredBlockOffsetZField.setPlaceholder(Text.literal("Z"));
		this.addSelectableChild(this.newTriggeredBlockOffsetZField);

		this.toggleNewTriggeredBlockResetsButton = this.addDrawableChild(CyclingButtonWidget.onOffBuilder(Text.translatable("gui.triggered_block.toggle_triggered_block_resets_button_label.on"), Text.translatable("gui.triggered_block.toggle_triggered_block_resets_button_label.off")).initially(this.newTriggeredBlockResets).omitKeyText().build(this.width / 2 + 8, 199, 146, 20, Text.empty(), (button, triggeredBlockResets) -> {
			this.newTriggeredBlockResets = triggeredBlockResets;
		}));

		this.saveButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.done()).dimensions(this.width / 2 - 4 - 150, 223, 150, 20).build());
		this.cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.cancel()).dimensions(this.width / 2 + 4, 223, 150, 20).build());

		this.updateWidgets();
	}

	private void updateWidgets() {

		this.cycleModeBackwardsButton.visible = false;
		this.cycleModeForwardsButton.visible = false;

		this.removeTriggeredBlockButton0.visible = false;
		this.removeTriggeredBlockButton1.visible = false;
		this.removeTriggeredBlockButton2.visible = false;
		this.removeTriggeredBlockButton3.visible = false;

		this.addNewTriggeredBlockButton.visible = false;
		this.newTriggeredBlockIdentifierField.setVisible(false);

		this.newTriggeredBlockOffsetXField.setVisible(false);
		this.newTriggeredBlockOffsetYField.setVisible(false);
		this.newTriggeredBlockOffsetZField.setVisible(false);

		this.toggleNewTriggeredBlockResetsButton.visible = false;

		this.saveButton.visible = false;
		this.cancelButton.visible = false;

		this.cycleModeBackwardsButton.visible = true;
		this.cycleModeForwardsButton.visible = true;

		int index = 0;
		for (int i = 0; i < Math.min(5, this.triggeredBlocksList.size()); i++) {
			if (index == 0) {
				this.removeTriggeredBlockButton0.visible = true;
			} else if (index == 1) {
				this.removeTriggeredBlockButton1.visible = true;
			} else if (index == 2) {
				this.removeTriggeredBlockButton2.visible = true;
			} else if (index == 3) {
				this.removeTriggeredBlockButton3.visible = true;
			}
			index++;
		}

		this.addNewTriggeredBlockButton.visible = true;
		this.newTriggeredBlockIdentifierField.setVisible(true);

		if (this.useScriptBlocksMode) {
			this.newTriggeredBlockOffsetXField.setVisible(true);
			this.newTriggeredBlockOffsetYField.setVisible(true);
			this.newTriggeredBlockOffsetZField.setVisible(true);
			this.toggleNewTriggeredBlockResetsButton.visible = true;
		}

		this.saveButton.visible = true;
		this.cancelButton.visible = true;

	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.newTriggeredBlockIdentifierField);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderInGameBackground(context);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		List<MutablePair<String, MutablePair<BlockPos, Boolean>>> list = new ArrayList<>(this.triggeredBlocksList);
		boolean bool = this.useScriptBlocksMode;
		boolean bool1 = this.newTriggeredBlockResets;
		int number = this.scrollPosition;
		float number1 = this.scrollAmount;
		String string = this.newTriggeredBlockIdentifierField.getText();
		String string1 = this.newTriggeredBlockOffsetXField.getText();
		String string2 = this.newTriggeredBlockOffsetYField.getText();
		String string3 = this.newTriggeredBlockOffsetZField.getText();
		this.init(client, width, height);
		this.triggeredBlocksList.clear();
		this.triggeredBlocksList.addAll(list);
		this.useScriptBlocksMode = bool;
		this.newTriggeredBlockResets = bool1;
		this.scrollPosition = number;
		this.scrollAmount = number1;
		this.newTriggeredBlockIdentifierField.setText(string);
		this.newTriggeredBlockOffsetXField.setText(string1);
		this.newTriggeredBlockOffsetYField.setText(string2);
		this.newTriggeredBlockOffsetZField.setText(string3);
		this.updateWidgets();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.mouseClicked = false;
		if (this.triggeredBlocksList.size() > 4) {
			int i = this.width / 2 - 152;
			int j = 45;
			if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 90)) {
				this.mouseClicked = true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.triggeredBlocksList.size() > 4
				&& this.mouseClicked) {
			int i = this.triggeredBlocksList.size() - 4;
			float f = (float) deltaY / (float) i;
			this.scrollAmount = MathHelper.clamp(this.scrollAmount + f, 0.0f, 1.0f);
			this.scrollPosition = (int) ((double) (this.scrollAmount * (float) i));
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (this.triggeredBlocksList.size() > 4
				&& mouseX >= (double) (this.width / 2 - 152) && mouseX <= (double) (this.width / 2 + 154)
				&& mouseY >= 44 && mouseY <= 136) {
			int i = this.triggeredBlocksList.size() - 4;
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

		int textWidth = this.textRenderer.getWidth(this.useScriptBlocksMode ? SCRIPT_BLOCKS_LABEL_TEXT : RED_STONE_MODE_LABEL_TEXT);
		context.drawTextWithShadow(this.textRenderer, this.useScriptBlocksMode ? SCRIPT_BLOCKS_LABEL_TEXT : RED_STONE_MODE_LABEL_TEXT, (this.width - textWidth) / 2, 26, 0xA0A0A0);

		for (int i = this.scrollPosition; i < Math.min(this.scrollPosition + 4, this.triggeredBlocksList.size()); i++) {
			MutablePair<String, MutablePair<BlockPos, Boolean>> triggeredBlock = this.triggeredBlocksList.get(i);
			if (this.useScriptBlocksMode) {
				BlockPos triggeredBlockOffset = triggeredBlock.getRight().getLeft();
				context.drawTextWithShadow(this.textRenderer,
						Text.translatable("gui.path_finding_end_node_block.entry_list.script_blocks_mode.line_1", triggeredBlock.getLeft()),
						this.width / 2 - 117, 45 + ((i - this.scrollPosition) * 24), 0xA0A0A0);
				context.drawTextWithShadow(this.textRenderer,
						Text.translatable(triggeredBlock.getRight().getRight() ? "gui.path_finding_end_node_block.entry_list.script_blocks_mode.line_2.reset" : "gui.path_finding_end_node_block.entry_list.script_blocks_mode.line_2.trigger", triggeredBlockOffset.getX(), triggeredBlockOffset.getY(), triggeredBlockOffset.getZ()),
						this.width / 2 - 117, 55 + ((i - this.scrollPosition) * 24), 0xA0A0A0);
			} else {
				context.drawTextWithShadow(this.textRenderer,
						Text.translatable("gui.path_finding_end_node_block.entry_list.red_stone_mode", triggeredBlock.getLeft()),
						this.width / 2 - 117, 50 + ((i - this.scrollPosition) * 24), 0xA0A0A0);
			}
		}
		if (this.triggeredBlocksList.size() > 4) {
			context.drawGuiTexture(SCROLL_BAR_BACKGROUND_8_92_TEXTURE, this.width / 2 - 153, 44, 8, 92);
			int k = (int) (83.0f * this.scrollAmount);
			context.drawGuiTexture(SCROLLER_TEXTURE, this.width / 2 - 152, 44 + 1 + k, 6, 7);
		}
//		context.drawTextWithShadow(this.textRenderer, NEW_IDENTIFIER_LABEL_TEXT, this.width / 2 - 153, 165, 0xA0A0A0);
		this.newTriggeredBlockIdentifierField.render(context, mouseX, mouseY, delta);
		if (this.useScriptBlocksMode) {
			context.drawTextWithShadow(this.textRenderer, NEW_BLOCK_OFFSET_LABEL_TEXT, this.width / 2 - 153, 189, 0xA0A0A0);
		}
		this.newTriggeredBlockOffsetXField.render(context, mouseX, mouseY, delta);
		this.newTriggeredBlockOffsetYField.render(context, mouseX, mouseY, delta);
		this.newTriggeredBlockOffsetZField.render(context, mouseX, mouseY, delta);

	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	private boolean updatePathFindingEndNodeBlock() {
		ClientPlayNetworking.send(new UpdatePathFindingEndNodeBlockPacket(
				this.pathFindingEndNodeBlockEntity.getPos(),
				this.useScriptBlocksMode,
				this.triggeredBlocksList
		));
		return true;
	}

}
