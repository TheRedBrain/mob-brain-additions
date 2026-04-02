package com.github.theredbrain.mobbrainadditions.gui.screen.ingame;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingNodeBlockPacket;
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
import java.util.Map;

@Environment(value = EnvType.CLIENT)
public class PathFindingNodeBlockScreen extends Screen {
	private static final Text NEW_NODE_POSITION_OFFSET_LABEL_TEXT = Text.translatable("gui.path_finding_node_block.new_node_position_offset");
	private static final Text NEW_NODE_IDENTIFIER_LABEL_TEXT = Text.translatable("gui.path_finding_node_block.new_node_identifier");
	private static final Text ADD_NEW_NODE_BUTTON_LABEL_TEXT = Text.translatable("gui.path_finding_node_block.add_new_node_button_label");
	private static final Identifier SCROLL_BAR_BACKGROUND_8_92_TEXTURE = MobBrainAdditions.identifier("scroll_bar/scroll_bar_background_8_92");
	private static final Identifier SCROLLER_TEXTURE = MobBrainAdditions.identifier("scroll_bar/scroller_vertical_6_7");
	public static final ButtonTextures REMOVE_ENTRY_BUTTON_TEXTURES = new ButtonTextures(
			MobBrainAdditions.identifier("widgets/remove_entry_button"), MobBrainAdditions.identifier("widgets/remove_entry_button_highlighted")
	);
	private final PathFindingNodeBlockEntity pathFindingNodeBlockEntity;
	private ButtonWidget removeNodeButton0;
	private ButtonWidget removeNodeButton1;
	private ButtonWidget removeNodeButton2;
	private ButtonWidget removeNodeButton3;
	private TextFieldWidget newNodeIdentifierField;
	private TextFieldWidget newNodePositionOffsetXField;
	private TextFieldWidget newNodePositionOffsetYField;
	private TextFieldWidget newNodePositionOffsetZField;
	private ButtonWidget addNewNodeButton;

	private ButtonWidget saveButton;
	private ButtonWidget cancelButton;

	private final List<MutablePair<String, BlockPos>> nodesList = new ArrayList<>();

	private int scrollPosition = 0;
	private float scrollAmount = 0.0f;
	private boolean mouseClicked = false;

	public PathFindingNodeBlockScreen(PathFindingNodeBlockEntity pathFindingNodeBlockEntity) {
		super(NarratorManager.EMPTY);
		this.pathFindingNodeBlockEntity = pathFindingNodeBlockEntity;
	}

	private void addNewNode() {
		String newEntranceName = this.newNodeIdentifierField.getText();
		if (newEntranceName.isEmpty()) {
			return;
		}
		int indexToRemove = -1;
		for (int i = 0; i < this.nodesList.size(); i++) {
			if (this.nodesList.get(i).getLeft().equals(newEntranceName)) {
				indexToRemove = i;
				break;
			}
		}
		if (indexToRemove != -1) {
			this.nodesList.remove(indexToRemove);
		}
		this.nodesList.add(new MutablePair<>(
						this.newNodeIdentifierField.getText(),
						new BlockPos(
								ParsingUtils.parseInt(this.newNodePositionOffsetXField.getText()),
								ParsingUtils.parseInt(this.newNodePositionOffsetYField.getText()),
								ParsingUtils.parseInt(this.newNodePositionOffsetZField.getText())
						)
				)
		);
		this.scrollPosition = 0;
		this.scrollAmount = 0.0f;
		this.updateWidgets();
	}

	private void removeNode(int index) {
		int actualIndex = index + this.scrollPosition;
		if (this.nodesList.size() > actualIndex) {
			this.nodesList.remove(actualIndex);
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
		this.nodesList.clear();

		List<Map.Entry<String, BlockPos>> entryList = this.pathFindingNodeBlockEntity.getNodes().entrySet().stream().toList();
		for (Map.Entry<String, BlockPos> entry : entryList) {
			this.nodesList.add(new MutablePair<>(entry.getKey(), entry.getValue()));
		}

		super.init();

		this.removeNodeButton0 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 20, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(0)));
		this.removeNodeButton1 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 44, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(1)));
		this.removeNodeButton2 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 68, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(2)));
		this.removeNodeButton3 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 92, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(3)));

		this.addNewNodeButton = this.addDrawableChild(ButtonWidget.builder(ADD_NEW_NODE_BUTTON_LABEL_TEXT, button -> this.addNewNode()).dimensions(this.width / 2 - 4 - 150, 116, 308, 20).build());

		this.newNodeIdentifierField = new TextFieldWidget(this.textRenderer, this.width / 2 - 154, 151, 300, 20, Text.empty());
		this.newNodeIdentifierField.setMaxLength(128);
		this.addSelectableChild(this.newNodeIdentifierField);

		this.newNodePositionOffsetXField = new TextFieldWidget(this.textRenderer, this.width / 2 - 154, 186, 100, 20, Text.empty());
		this.newNodePositionOffsetXField.setMaxLength(128);
		this.addSelectableChild(this.newNodePositionOffsetXField);

		this.newNodePositionOffsetYField = new TextFieldWidget(this.textRenderer, this.width / 2 - 50, 186, 100, 20, Text.empty());
		this.newNodePositionOffsetYField.setMaxLength(128);
		this.addSelectableChild(this.newNodePositionOffsetYField);

		this.newNodePositionOffsetZField = new TextFieldWidget(this.textRenderer, this.width / 2 + 54, 186, 100, 20, Text.empty());
		this.newNodePositionOffsetZField.setMaxLength(128);
		this.addSelectableChild(this.newNodePositionOffsetZField);

		this.saveButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.done()).dimensions(this.width / 2 - 4 - 150, 210, 150, 20).build());
		this.cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.cancel()).dimensions(this.width / 2 + 4, 210, 150, 20).build());

		this.updateWidgets();
	}

	private void updateWidgets() {

		this.removeNodeButton0.visible = false;
		this.removeNodeButton1.visible = false;
		this.removeNodeButton2.visible = false;
		this.removeNodeButton3.visible = false;

		this.newNodeIdentifierField.setVisible(false);
		this.newNodePositionOffsetXField.setVisible(false);
		this.newNodePositionOffsetYField.setVisible(false);
		this.newNodePositionOffsetZField.setVisible(false);
		this.addNewNodeButton.visible = false;

		this.saveButton.visible = false;
		this.cancelButton.visible = false;

		int index = 0;
		for (int i = 0; i < Math.min(4, this.nodesList.size()); i++) {
			if (index == 0) {
				this.removeNodeButton0.visible = true;
			} else if (index == 1) {
				this.removeNodeButton1.visible = true;
			} else if (index == 2) {
				this.removeNodeButton2.visible = true;
			} else if (index == 3) {
				this.removeNodeButton3.visible = true;
			}
			index++;
		}

		this.newNodePositionOffsetXField.setVisible(true);
		this.newNodePositionOffsetYField.setVisible(true);
		this.newNodePositionOffsetZField.setVisible(true);
		this.newNodeIdentifierField.setVisible(true);
		this.addNewNodeButton.visible = true;

		this.saveButton.visible = true;
		this.cancelButton.visible = true;

	}

	@Override
	protected void setInitialFocus() {
		this.setInitialFocus(this.newNodeIdentifierField);
	}

	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderInGameBackground(context);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		List<MutablePair<String, BlockPos>> list = new ArrayList<>(this.nodesList);
		int number = this.scrollPosition;
		float number1 = this.scrollAmount;
		String string = this.newNodeIdentifierField.getText();
		String string1 = this.newNodePositionOffsetXField.getText();
		String string2 = this.newNodePositionOffsetYField.getText();
		String string3 = this.newNodePositionOffsetZField.getText();
		this.init(client, width, height);
		this.nodesList.clear();
		this.nodesList.addAll(list);
		this.scrollPosition = number;
		this.scrollAmount = number1;
		this.newNodeIdentifierField.setText(string);
		this.newNodePositionOffsetXField.setText(string1);
		this.newNodePositionOffsetYField.setText(string2);
		this.newNodePositionOffsetZField.setText(string3);
		this.updateWidgets();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.mouseClicked = false;
		if (this.nodesList.size() > 4) {
			int i = this.width / 2 - 152;
			int j = 21;
			if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 90)) {
				this.mouseClicked = true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.nodesList.size() > 4
				&& this.mouseClicked) {
			int i = this.nodesList.size() - 4;
			float f = (float) deltaY / (float) i;
			this.scrollAmount = MathHelper.clamp(this.scrollAmount + f, 0.0f, 1.0f);
			this.scrollPosition = (int) ((double) (this.scrollAmount * (float) i));
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (this.nodesList.size() > 4
				&& mouseX >= (double) (this.width / 2 - 152) && mouseX <= (double) (this.width / 2 + 154)
				&& mouseY >= 20 && mouseY <= 112) {
			int i = this.nodesList.size() - 4;
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

		for (int i = this.scrollPosition; i < Math.min(this.scrollPosition + 4, this.nodesList.size()); i++) {
			MutablePair<String, BlockPos> node = this.nodesList.get(i);
			BlockPos nodeOffset = node.getRight();
			context.drawTextWithShadow(this.textRenderer,
					Text.translatable("gui.path_finding_node_block.node_list.id", node.getLeft()),
					this.width / 2 - 117, 21 + ((i - this.scrollPosition) * 24), 0xA0A0A0);
			context.drawTextWithShadow(this.textRenderer,
					Text.translatable("gui.path_finding_node_block.node_list.offset", nodeOffset.getX(), nodeOffset.getY(), nodeOffset.getZ()),
					this.width / 2 - 117, 31 + ((i - this.scrollPosition) * 24), 0xA0A0A0);
		}
		if (this.nodesList.size() > 4) {
			context.drawGuiTexture(SCROLL_BAR_BACKGROUND_8_92_TEXTURE, this.width / 2 - 153, 20, 8, 92);
			int k = (int) (83.0f * this.scrollAmount);
			context.drawGuiTexture(SCROLLER_TEXTURE, this.width / 2 - 152, 20 + 1 + k, 6, 7);
		}
		context.drawTextWithShadow(this.textRenderer, NEW_NODE_IDENTIFIER_LABEL_TEXT, this.width / 2 - 153, 141, 0xA0A0A0);
		this.newNodeIdentifierField.render(context, mouseX, mouseY, delta);

		int textWidth = textRenderer.getWidth(NEW_NODE_POSITION_OFFSET_LABEL_TEXT);
		context.drawTextWithShadow(this.textRenderer, NEW_NODE_POSITION_OFFSET_LABEL_TEXT, (this.width - textWidth) / 2, 176, 0xA0A0A0);
		this.newNodePositionOffsetXField.render(context, mouseX, mouseY, delta);
		this.newNodePositionOffsetYField.render(context, mouseX, mouseY, delta);
		this.newNodePositionOffsetZField.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	private boolean updatePathFindingNodeBlock() {
		ClientPlayNetworking.send(new UpdatePathFindingNodeBlockPacket(
				this.pathFindingNodeBlockEntity.getPos(),
				this.nodesList
		));
		return true;
	}

}
