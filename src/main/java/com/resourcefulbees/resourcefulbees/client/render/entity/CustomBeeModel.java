package com.resourcefulbees.resourcefulbees.client.render.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.resourcefulbees.resourcefulbees.config.Config;
import com.resourcefulbees.resourcefulbees.entity.passive.CustomBeeEntity;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.ModelUtils;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class CustomBeeModel<T extends CustomBeeEntity> extends AgeableModel<T> {
    private final ModelRenderer body;
    private final ModelRenderer torso;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing;
    private final ModelRenderer frontLegs;
    private final ModelRenderer middleLegs;
    private final ModelRenderer backLegs;
    private final ModelRenderer stinger;
    private final ModelRenderer leftAntenna;
    private final ModelRenderer rightAntenna;
    private float bodyPitch;

    private float beeSize = 0;

    public CustomBeeModel() {
        super(false, 24.0F, 0.0F);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.torso = new ModelRenderer(this, 0, 0);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        this.torso.addCuboid(-3.5F, -4.0F, -5.0F, 7.0F, 7.0F, 10.0F, 0.0F);
        this.stinger = new ModelRenderer(this, 26, 7);
        this.stinger.addCuboid(0.0F, -1.0F, 5.0F, 0.0F, 1.0F, 2.0F, 0.0F);
        this.torso.addChild(this.stinger);
        this.leftAntenna = new ModelRenderer(this, 2, 0);
        this.leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.leftAntenna.addCuboid(1.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.rightAntenna = new ModelRenderer(this, 2, 3);
        this.rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.rightAntenna.addCuboid(-2.5F, -2.0F, -3.0F, 1.0F, 2.0F, 3.0F, 0.0F);
        this.torso.addChild(this.leftAntenna);
        this.torso.addChild(this.rightAntenna);
        this.rightWing = new ModelRenderer(this, 0, 18);
        this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        this.rightWing.rotateAngleX = 0.0F;
        this.rightWing.rotateAngleY = -0.2618F;
        this.rightWing.rotateAngleZ = 0.0F;
        this.body.addChild(this.rightWing);
        this.rightWing.addCuboid(-9.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.leftWing = new ModelRenderer(this, 0, 18);
        this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        this.leftWing.rotateAngleX = 0.0F;
        this.leftWing.rotateAngleY = 0.2618F;
        this.leftWing.rotateAngleZ = 0.0F;
        this.leftWing.mirror = true;
        this.body.addChild(this.leftWing);
        this.leftWing.addCuboid(0.0F, 0.0F, 0.0F, 9.0F, 0.0F, 6.0F, 0.001F);
        this.frontLegs = new ModelRenderer(this);
        this.frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        this.body.addChild(this.frontLegs);
        this.frontLegs.func_217178_a("frontLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 1);
        this.middleLegs = new ModelRenderer(this);
        this.middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        this.body.addChild(this.middleLegs);
        this.middleLegs.func_217178_a("midLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 3);
        this.backLegs = new ModelRenderer(this);
        this.backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        this.body.addChild(this.backLegs);
        this.backLegs.func_217178_a("backLegBox", -5.0F, 0.0F, 0.0F, 7, 2, 0, 0.0F, 26, 5);

    }


    public void setLivingAnimations(@Nonnull T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        this.bodyPitch = entityIn.getBodyPitch(partialTick);
        this.stinger.showModel = !entityIn.hasStung();
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setAngles(CustomBeeEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.rightWing.rotateAngleX = 0.0F;
        this.leftAntenna.rotateAngleX = 0.0F;
        this.rightAntenna.rotateAngleX = 0.0F;
        this.body.rotateAngleX = 0.0F;
        this.body.rotationPointY = 19.0F;
        boolean flag = entityIn.isOnGround() && entityIn.getMotion().lengthSquared() < 1.0E-7D;
        if (flag) {
            this.rightWing.rotateAngleY = -0.2618F;
            this.rightWing.rotateAngleZ = 0.0F;
            this.leftWing.rotateAngleX = 0.0F;
            this.leftWing.rotateAngleY = 0.2618F;
            this.leftWing.rotateAngleZ = 0.0F;
            this.frontLegs.rotateAngleX = 0.0F;
            this.middleLegs.rotateAngleX = 0.0F;
            this.backLegs.rotateAngleX = 0.0F;
        } else {
            this.rightWing.rotateAngleY = 0.0F;
            this.rightWing.rotateAngleZ = MathHelper.cos((ageInTicks % 98000 * 2.1F)) * (float)Math.PI * 0.15F;
            this.leftWing.rotateAngleX = this.rightWing.rotateAngleX;
            this.leftWing.rotateAngleY = this.rightWing.rotateAngleY;
            this.leftWing.rotateAngleZ = -this.rightWing.rotateAngleZ;
            this.frontLegs.rotateAngleX = ((float)Math.PI / 4F);
            this.middleLegs.rotateAngleX = ((float)Math.PI / 4F);
            this.backLegs.rotateAngleX = ((float)Math.PI / 4F);
            this.body.rotateAngleX = 0.0F;
            this.body.rotateAngleY = 0.0F;
            this.body.rotateAngleZ = 0.0F;
        }

        if (!entityIn.hasAngerTime()) {
            this.body.rotateAngleX = 0.0F;
            this.body.rotateAngleY = 0.0F;
            this.body.rotateAngleZ = 0.0F;
            if (!flag) {
                float f1 = MathHelper.cos(ageInTicks * 0.18F);
                this.body.rotateAngleX = 0.1F + f1 * (float)Math.PI * 0.025F;
                this.leftAntenna.rotateAngleX = f1 * (float)Math.PI * 0.03F;
                this.rightAntenna.rotateAngleX = f1 * (float)Math.PI * 0.03F;
                this.frontLegs.rotateAngleX = -f1 * (float)Math.PI * 0.1F + ((float)Math.PI / 8F);
                this.backLegs.rotateAngleX = -f1 * (float)Math.PI * 0.05F + ((float)Math.PI / 4F);
                this.body.rotationPointY = 19.0F - MathHelper.cos(ageInTicks * 0.18F) * 0.9F;
            }
        }

        if (this.bodyPitch > 0.0F) {
            this.body.rotateAngleX = ModelUtils.interpolateAngle(this.body.rotateAngleX, 3.0915928F, this.bodyPitch);
        }

        beeSize = entityIn.getBeeData().getSizeModifier();
        if (isChild) beeSize *= Config.CHILD_SIZE_MODIFIER.get();
    }

    @Nonnull
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of();
    }

    @Nonnull
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        matrixStackIn.push();
        matrixStackIn.translate(0, 1.5 - beeSize * 1.5, 0);
        matrixStackIn.scale(beeSize, beeSize, beeSize);
        super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.pop();
    }
}
