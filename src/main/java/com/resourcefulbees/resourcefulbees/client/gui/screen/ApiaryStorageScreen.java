package com.resourcefulbees.resourcefulbees.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.client.gui.widget.TabImageButton;
import com.resourcefulbees.resourcefulbees.container.ApiaryStorageContainer;
import com.resourcefulbees.resourcefulbees.lib.ApiaryTabs;
import com.resourcefulbees.resourcefulbees.network.NetPacketHandler;
import com.resourcefulbees.resourcefulbees.network.packets.ApiaryTabMessage;
import com.resourcefulbees.resourcefulbees.registry.RegistryHandler;
import com.resourcefulbees.resourcefulbees.tileentity.multiblocks.apiary.ApiaryStorageTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class ApiaryStorageScreen extends ContainerScreen<ApiaryStorageContainer> {

    private static final ResourceLocation BACKGROUND_1X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_9.png");
    private static final ResourceLocation BACKGROUND_3X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_27.png");
    private static final ResourceLocation BACKGROUND_6X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_54.png");
    private static final ResourceLocation BACKGROUND_9X9 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_81.png");
    private static final ResourceLocation BACKGROUND_9X12 = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_storage_108.png");
    private static final ResourceLocation TABS_BG = new ResourceLocation(ResourcefulBees.MOD_ID, "textures/gui/apiary/apiary_gui_tabs.png");

    public ApiaryStorageTileEntity apiaryStorageTileEntity;

    public ResourceLocation background;

    private TabImageButton mainTabButton;
    private TabImageButton breedTabButton;

    public ApiaryStorageScreen(ApiaryStorageContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);

        preInit();
    }

    protected void preInit(){
        this.xSize = 226;

        switch (this.container.numberOfSlots) {
            case 27:
                this.ySize = 168;
                background = BACKGROUND_3X9;
                break;
            case 54:
                this.ySize = 222;
                background = BACKGROUND_6X9;
                break;
            case 81:
                this.ySize = 276;
                background = BACKGROUND_9X9;
                break;
            case 108:
                this.xSize = 281;
                this.ySize = 276;
                background = BACKGROUND_9X12;
                break;
            default:
                this.ySize = 132;
                background = BACKGROUND_1X9;
        }
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();

        apiaryStorageTileEntity = this.container.apiaryStorageTileEntity;

        int i = this.guiLeft;
        int j = this.guiTop;
        int t = i + this.xSize - 23;

        mainTabButton = this.addButton(new TabImageButton(t+1, j+17, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(RegistryHandler.BEE_JAR.get()), 1, 1,
                (onPress) -> this.changeScreen(ApiaryTabs.MAIN)) {
            public void renderToolTip(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
                StringTextComponent s = new StringTextComponent(I18n.format("gui.resourcefulbees.apiary.button.main_screen"));
                ApiaryStorageScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });

        this.addButton(new TabImageButton(t + 1, j + 37, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(Items.HONEYCOMB), 2, 1,
                (onPress) -> this.changeScreen(ApiaryTabs.STORAGE)) {
            public void renderToolTip(@Nonnull MatrixStack matrix,int mouseX, int mouseY) {
                StringTextComponent s = new StringTextComponent(I18n.format("gui.resourcefulbees.apiary.button.storage_screen"));
                ApiaryStorageScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        }).active = false;

        breedTabButton = this.addButton(new TabImageButton(t + 1, j + 57, 18, 18, 110, 0, 18, TABS_BG, new ItemStack(RegistryHandler.GOLD_FLOWER_ITEM.get()), 1, 1,
                (onPress) -> this.changeScreen(ApiaryTabs.BREED)) {
            public void renderToolTip(@Nonnull MatrixStack matrix,int mouseX, int mouseY) {
                StringTextComponent s = new StringTextComponent(I18n.format("gui.resourcefulbees.apiary.button.breed_screen"));
                ApiaryStorageScreen.this.renderTooltip(matrix, s, mouseX, mouseY);
            }
        });
    }

    private void changeScreen(ApiaryTabs tab) {
        switch (tab) {
            case BREED:
                if (breedTabButton.active)
                    NetPacketHandler.sendToServer(new ApiaryTabMessage(apiaryStorageTileEntity.getPos(), ApiaryTabs.BREED));
                break;
            case STORAGE:
                break;
            case MAIN:
                if (mainTabButton.active)
                    NetPacketHandler.sendToServer(new ApiaryTabMessage(apiaryStorageTileEntity.getPos(), ApiaryTabs.MAIN));
        }
    }

    @Override
    public void render(@Nonnull MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, partialTicks);
        this.drawMouseoverTooltip(matrix, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(@Nonnull MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
        if (this.container.rebuild) {
            preInit();
            init();
            this.container.rebuild = false;
        }

            mainTabButton.active = apiaryStorageTileEntity.getApiary() != null;
            breedTabButton.active = apiaryStorageTileEntity.getApiary() != null && apiaryStorageTileEntity.getApiary().breederPos != null;

        Minecraft client = this.client;
        if (client != null) {
            this.client.getTextureManager().bindTexture(background);
            int i = this.guiLeft;
            int j = this.guiTop;
            drawTexture(matrix, i + 26, j, 0, 0, this.xSize, this.ySize, 384, 384);
            drawTexture(matrix, i + 1, j + 12, 359, 0, 25, 28, 384, 384);
            int t = i + this.xSize - 23;
            this.client.getTextureManager().bindTexture(TABS_BG);
            drawTexture(matrix, t -1, j + 12, 0,0, 25, 68, 128, 128);
        }
    }

    @Override
    protected void drawForeground(@Nonnull MatrixStack matrix, int mouseX, int mouseY) {
        for (Widget widget : this.buttons) {
            if (widget.isHovered()) {
                widget.renderToolTip(matrix, mouseX - this.guiLeft, mouseY - this.guiTop);
                break;
            }
        }
    }
}
