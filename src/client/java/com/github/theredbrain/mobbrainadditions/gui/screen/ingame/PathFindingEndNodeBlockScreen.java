package com.github.theredbrain.mobbrainadditions.gui.screen.ingame;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingEndNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingEndNodeBlockPacket;
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
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@Environment(value = EnvType.CLIENT)
public class PathFindingEndNodeBlockScreen extends Screen {
	private static final Text NEW_NODE_IDENTIFIER_LABEL_TEXT = Text.translatable("gui.path_finding_node_end_block.new_node_identifier");
	private static final Text ADD_NEW_NODE_IDENTIFIER_BUTTON_LABEL_TEXT = Text.translatable("gui.path_finding_node_end_block.add_new_node_identifier_button_label");
	private static final Identifier SCROLL_BAR_BACKGROUND_8_116_TEXTURE = MobBrainAdditions.identifier("scroll_bar/scroll_bar_background_8_116");
	private static final Identifier SCROLLER_TEXTURE = MobBrainAdditions.identifier("scroll_bar/scroller_vertical_6_7");
	public static final ButtonTextures REMOVE_ENTRY_BUTTON_TEXTURES = new ButtonTextures(
			MobBrainAdditions.identifier("widgets/remove_entry_button"), MobBrainAdditions.identifier("widgets/remove_entry_button_highlighted")
	);
	private final PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity;
	private ButtonWidget removeNodeButton0;
	private ButtonWidget removeNodeButton1;
	private ButtonWidget removeNodeButton2;
	private ButtonWidget removeNodeButton3;
	private ButtonWidget removeNodeButton4;
	private ButtonWidget addNewNodeButton;
	private TextFieldWidget newNodeIdentifierField;

	private ButtonWidget saveButton;
	private ButtonWidget cancelButton;

	private final List<String> nodeIdsList = new ArrayList<>();

	private int scrollPosition = 0;
	private float scrollAmount = 0.0f;
	private boolean mouseClicked = false;

	public PathFindingEndNodeBlockScreen(PathFindingEndNodeBlockEntity pathFindingEndNodeBlockEntity) {
		super(NarratorManager.EMPTY);
		this.pathFindingEndNodeBlockEntity = pathFindingEndNodeBlockEntity;
	}

	private void addNewNode() {
		String newNodeId = this.newNodeIdentifierField.getText();
		if (!newNodeId.isEmpty() && !this.nodeIdsList.contains(newNodeId)) {
			this.nodeIdsList.add(newNodeId);
		}
		this.scrollPosition = 0;
		this.scrollAmount = 0.0f;
		this.updateWidgets();
	}

	private void removeNode(int index) {
		int actualIndex = index + this.scrollPosition;
		if (this.nodeIdsList.size() > actualIndex) {
			this.nodeIdsList.remove(actualIndex);
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
		this.nodeIdsList.clear();

		this.nodeIdsList.addAll(this.pathFindingEndNodeBlockEntity.getNodeIds());

		super.init();

		this.removeNodeButton0 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 20, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(0)));
		this.removeNodeButton1 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 44, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(1)));
		this.removeNodeButton2 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 68, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(2)));
		this.removeNodeButton3 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 92, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(3)));
		this.removeNodeButton4 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 116, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeNode(4)));

		this.addNewNodeButton = this.addDrawableChild(ButtonWidget.builder(ADD_NEW_NODE_IDENTIFIER_BUTTON_LABEL_TEXT, button -> this.addNewNode()).dimensions(this.width / 2 - 4 - 150, 140, 308, 20).build());

		this.newNodeIdentifierField = new TextFieldWidget(this.textRenderer, this.width / 2 - 154, 175, 300, 20, Text.empty());
		this.newNodeIdentifierField.setMaxLength(128);
		this.addSelectableChild(this.newNodeIdentifierField);

		this.saveButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.done()).dimensions(this.width / 2 - 4 - 150, 210, 150, 20).build());
		this.cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.cancel()).dimensions(this.width / 2 + 4, 210, 150, 20).build());

		this.updateWidgets();
	}

	private void updateWidgets() {

		this.removeNodeButton0.visible = false;
		this.removeNodeButton1.visible = false;
		this.removeNodeButton2.visible = false;
		this.removeNodeButton3.visible = false;
		this.removeNodeButton4.visible = false;

		this.addNewNodeButton.visible = false;
		this.newNodeIdentifierField.setVisible(false);

		this.saveButton.visible = false;
		this.cancelButton.visible = false;

		int index = 0;
		for (int i = 0; i < Math.min(5, this.nodeIdsList.size()); i++) {
			if (index == 0) {
				this.removeNodeButton0.visible = true;
			} else if (index == 1) {
				this.removeNodeButton1.visible = true;
			} else if (index == 2) {
				this.removeNodeButton2.visible = true;
			} else if (index == 3) {
				this.removeNodeButton3.visible = true;
			} else if (index == 4) {
				this.removeNodeButton4.visible = true;
			}
			index++;
		}

		this.addNewNodeButton.visible = true;
		this.newNodeIdentifierField.setVisible(true);

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
		List<String> list = new ArrayList<>(this.nodeIdsList);
		int number = this.scrollPosition;
		float number1 = this.scrollAmount;
		String string = this.newNodeIdentifierField.getText();
		this.init(client, width, height);
		this.nodeIdsList.clear();
		this.nodeIdsList.addAll(list);
		this.scrollPosition = number;
		this.scrollAmount = number1;
		this.newNodeIdentifierField.setText(string);
		this.updateWidgets();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.mouseClicked = false;
		if (this.nodeIdsList.size() > 5) {
			int i = this.width / 2 - 152;
			int j = 21;
			if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 114)) {
				this.mouseClicked = true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.nodeIdsList.size() > 5
				&& this.mouseClicked) {
			int i = this.nodeIdsList.size() - 5;
			float f = (float) deltaY / (float) i;
			this.scrollAmount = MathHelper.clamp(this.scrollAmount + f, 0.0f, 1.0f);
			this.scrollPosition = (int) ((double) (this.scrollAmount * (float) i));
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (this.nodeIdsList.size() > 5
				&& mouseX >= (double) (this.width / 2 - 152) && mouseX <= (double) (this.width / 2 + 154)
				&& mouseY >= 20 && mouseY <= 136) {
			int i = this.nodeIdsList.size() - 5;
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

		for (int i = this.scrollPosition; i < Math.min(this.scrollPosition + 5, this.nodeIdsList.size()); i++) {
			context.drawTextWithShadow(this.textRenderer,
					this.nodeIdsList.get(i),
					this.width / 2 - 117, 26 + ((i - this.scrollPosition) * 24), 0xA0A0A0);
		}
		if (this.nodeIdsList.size() > 5) {
			context.drawGuiTexture(SCROLL_BAR_BACKGROUND_8_116_TEXTURE, this.width / 2 - 153, 20, 8, 116);
			int k = (int) (107.0f * this.scrollAmount);
			context.drawGuiTexture(SCROLLER_TEXTURE, this.width / 2 - 152, 20 + 1 + k, 6, 7);
		}
		context.drawTextWithShadow(this.textRenderer, NEW_NODE_IDENTIFIER_LABEL_TEXT, this.width / 2 - 153, 165, 0xA0A0A0);
		this.newNodeIdentifierField.render(context, mouseX, mouseY, delta);

	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	private boolean updatePathFindingEndNodeBlock() {
		ClientPlayNetworking.send(new UpdatePathFindingEndNodeBlockPacket(
				this.pathFindingEndNodeBlockEntity.getPos(),
				this.nodeIdsList
		));
		return true;
	}

}
