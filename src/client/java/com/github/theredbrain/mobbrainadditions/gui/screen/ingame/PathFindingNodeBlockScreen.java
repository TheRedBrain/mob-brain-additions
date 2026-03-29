package com.github.theredbrain.mobbrainadditions.gui.screen.ingame;

import com.github.theredbrain.mobbrainadditions.MobBrainAdditions;
import com.github.theredbrain.mobbrainadditions.block.entity.PathFindingNodeBlockEntity;
import com.github.theredbrain.mobbrainadditions.network.packet.UpdatePathFindingNodeBlockPacket;
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
	private static final Text REMOVE_LIST_ENTRY_BUTTON_LABEL_TEXT = Text.translatable("gui.path_finding_node_block.remove_list_entry_button_label");
	private static final Text NEW_NODE_POSITION_OFFSET_LABEL_TEXT = Text.translatable("gui.path_finding_node_block.new_node_position_offset");
	private static final Text NEW_NODE_IDENTIFIER_LABEL_TEXT = Text.translatable("gui.path_finding_node_block.new_node_identifier");
	private static final Text ADD_NEW_NODE_BUTTON_LABEL_TEXT = Text.translatable("gui.path_finding_node_block.add_new_node_button_label");
	private static final Identifier SCROLL_BAR_BACKGROUND_8_70_TEXTURE = MobBrainAdditions.identifier("scroll_bar/scroll_bar_background_8_70");
	private static final Identifier SCROLLER_TEXTURE = MobBrainAdditions.identifier("scroll_bar/scroller_vertical_6_7");
	public static final ButtonTextures REMOVE_ENTRY_BUTTON_TEXTURES = new ButtonTextures(
			MobBrainAdditions.identifier("widgets/remove_entry_button"), MobBrainAdditions.identifier("widgets/remove_entry_button_highlighted")
	);
	private final PathFindingNodeBlockEntity pathFindingNodeBlockEntity;
	private ButtonWidget removeSideEntranceButton0;
	private ButtonWidget removeSideEntranceButton1;
	private ButtonWidget removeSideEntranceButton2;
	private TextFieldWidget newNodeIdentifierField;
	private TextFieldWidget newNodePositionOffsetXField;
	private TextFieldWidget newNodePositionOffsetYField;
	private TextFieldWidget newNodePositionOffsetZField;
	private ButtonWidget addNewSideEntranceButton;

	private ButtonWidget saveButton;
	private ButtonWidget cancelButton;

	private List<MutablePair<String, BlockPos>> nodesList = new ArrayList<>();

	private int scrollPosition = 0;
	private float scrollAmount = 0.0f;
	private boolean mouseClicked = false;

	public PathFindingNodeBlockScreen(PathFindingNodeBlockEntity pathFindingNodeBlockEntity) {
		super(NarratorManager.EMPTY);
		this.pathFindingNodeBlockEntity = pathFindingNodeBlockEntity;
	}

	private void addNewSideEntrance() {
		String newEntranceName = this.newNodeIdentifierField.getText();
		if (newEntranceName.equals("")) {
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
								parseInt(this.newNodePositionOffsetXField.getText()),
								parseInt(this.newNodePositionOffsetYField.getText()),
								parseInt(this.newNodePositionOffsetZField.getText())
						)
				)
		);
		this.scrollPosition = 0;
		this.scrollAmount = 0.0f;
		this.updateWidgets();
	}

	private void removeSideEntrance(int index) {
		this.nodesList.remove(index + this.scrollPosition);
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

		this.removeSideEntranceButton0 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 44, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeSideEntrance(0)));
		this.removeSideEntranceButton1 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 68, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeSideEntrance(1)));
		this.removeSideEntranceButton2 = this.addDrawableChild(new TexturedButtonWidget(this.width / 2 - 141, 92, 20, 20, REMOVE_ENTRY_BUTTON_TEXTURES, button -> this.removeSideEntrance(2)));

		this.newNodeIdentifierField = new TextFieldWidget(this.textRenderer, this.width / 2 - 154, 127, 300, 20, Text.empty());
		this.newNodeIdentifierField.setMaxLength(128);
		this.addSelectableChild(this.newNodeIdentifierField);

		this.newNodePositionOffsetXField = new TextFieldWidget(this.textRenderer, this.width / 2 - 154, 162, 100, 20, Text.empty());
		this.newNodePositionOffsetXField.setMaxLength(128);
		this.addSelectableChild(this.newNodePositionOffsetXField);

		this.newNodePositionOffsetYField = new TextFieldWidget(this.textRenderer, this.width / 2 - 50, 162, 100, 20, Text.empty());
		this.newNodePositionOffsetYField.setMaxLength(128);
		this.addSelectableChild(this.newNodePositionOffsetYField);

		this.newNodePositionOffsetZField = new TextFieldWidget(this.textRenderer, this.width / 2 + 54, 162, 100, 20, Text.empty());
		this.newNodePositionOffsetZField.setMaxLength(128);
		this.addSelectableChild(this.newNodePositionOffsetZField);

		this.addNewSideEntranceButton = this.addDrawableChild(ButtonWidget.builder(ADD_NEW_NODE_BUTTON_LABEL_TEXT, button -> this.addNewSideEntrance()).dimensions(this.width / 2 - 4 - 150, 186, 308, 20).build());

		this.saveButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> this.done()).dimensions(this.width / 2 - 4 - 150, 210, 150, 20).build());
		this.cancelButton = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.cancel()).dimensions(this.width / 2 + 4, 210, 150, 20).build());

		this.updateWidgets();
	}

	private void updateWidgets() {

		this.removeSideEntranceButton0.visible = false;
		this.removeSideEntranceButton1.visible = false;
		this.removeSideEntranceButton2.visible = false;

		this.newNodeIdentifierField.setVisible(false);
		this.newNodePositionOffsetXField.setVisible(false);
		this.newNodePositionOffsetYField.setVisible(false);
		this.newNodePositionOffsetZField.setVisible(false);
		this.addNewSideEntranceButton.visible = false;

		this.saveButton.visible = false;
		this.cancelButton.visible = false;

		int index = 0;
		for (int i = 0; i < Math.min(3, this.nodesList.size()); i++) {
			if (index == 0) {
				this.removeSideEntranceButton0.visible = true;
			} else if (index == 1) {
				this.removeSideEntranceButton1.visible = true;
			} else if (index == 2) {
				this.removeSideEntranceButton2.visible = true;
			}
			index++;
		}

		this.newNodePositionOffsetXField.setVisible(true);
		this.newNodePositionOffsetYField.setVisible(true);
		this.newNodePositionOffsetZField.setVisible(true);
		this.newNodeIdentifierField.setVisible(true);
		this.addNewSideEntranceButton.visible = true;

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
		if (this.nodesList.size() > 3) {
			int i = this.width / 2 - 152;
			int j = 45;
			if (mouseX >= (double) i && mouseX < (double) (i + 6) && mouseY >= (double) j && mouseY < (double) (j + 68)) {
				this.mouseClicked = true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (this.nodesList.size() > 3
				&& this.mouseClicked) {
			int i = this.nodesList.size() - 3;
			float f = (float) deltaY / (float) i;
			this.scrollAmount = MathHelper.clamp(this.scrollAmount + f, 0.0f, 1.0f);
			this.scrollPosition = (int) ((double) (this.scrollAmount * (float) i));
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (this.nodesList.size() > 3
				&& mouseX >= (double) (this.width / 2 - 152) && mouseX <= (double) (this.width / 2 + 154)
				&& mouseY >= 44 && mouseY <= 114) {
			int i = this.nodesList.size() - 3;
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

		for (int i = this.scrollPosition; i < Math.min(this.scrollPosition + 3, this.nodesList.size()); i++) {
			String text = this.nodesList.get(i).getLeft();
			if (!this.nodesList.get(i).getLeft().isEmpty()) {
				text = this.nodesList.get(i).getLeft() + ", " + this.nodesList.get(i).getRight().toString();
			}
			context.drawTextWithShadow(this.textRenderer, text, this.width / 2 - 117, 50 + ((i - this.scrollPosition) * 24), 0xA0A0A0);
		}
		if (this.nodesList.size() > 3) {
			context.drawTexture(SCROLL_BAR_BACKGROUND_8_70_TEXTURE, this.width / 2 - 153, 44, 0, 0, 8, 70);
			int k = (int) (61.0f * this.scrollAmount);
			context.drawTexture(SCROLLER_TEXTURE, this.width / 2 - 152, 44 + 1 + k, 0, 0, 6, 7);
		}
		context.drawTextWithShadow(this.textRenderer, NEW_NODE_IDENTIFIER_LABEL_TEXT, this.width / 2 - 153, 116, 0xA0A0A0);
		this.newNodeIdentifierField.render(context, mouseX, mouseY, delta);
		context.drawTextWithShadow(this.textRenderer, NEW_NODE_POSITION_OFFSET_LABEL_TEXT, this.width / 2 - 153, 151, 0xA0A0A0);
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

	public static int parseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException numberFormatException) {
			return 0;
		}
	}

}
