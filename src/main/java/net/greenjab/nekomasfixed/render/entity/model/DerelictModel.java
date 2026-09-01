package net.greenjab.nekomasfixed.render.entity.model;

import net.greenjab.nekomasfixed.registry.entity.Derelict;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

/**
 * Render-state collapse: {@code ZombieModel<ZombieRenderState>} (package
 * {@code net.minecraft.client.model.monster.zombie}) → 1.20.1's real, flat-package
 * {@code net.minecraft.client.model.ZombieModel<T extends Zombie>}. No {@code setupAnim} override
 * existed, so vanilla {@code ZombieModel}'s own aggressive-pose animation is unchanged.
 *
 * <p>1.20.1 has <b>no</b> {@code BabyZombieModel} class (VERIFIED — absent from
 * {@code forge-1.20.1-mapped-src}, unlike 26.2). Vanilla baby zombies reuse the adult
 * {@code HumanoidModel} mesh and get their proportions from {@code HumanoidModel}'s own built-in
 * young/baby scale-down — a different, per-{@code HumanoidModel} mechanism from the
 * {@code AgeableListModel} one {@link MoobloomModel} needed to neutralise. So {@code BabyDerelictModel}
 * is deleted (not ported — nothing to port it onto) and {@code DerelictRenderer} uses this one mesh
 * for both ages, switching only the *texture* for the baby variant, exactly mirroring vanilla
 * Zombie/Husk/Drowned's own baby handling.
 */
public class DerelictModel extends ZombieModel<Derelict> {

    public DerelictModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static LayerDefinition createBodyLayer() {
        CubeDeformation g = CubeDeformation.NONE;
        MeshDefinition mesh = HumanoidModel.createMesh(g, 0.0F);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(1.9F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition createOuterLayer() {
        CubeDeformation g = new CubeDeformation(0.25F);
        MeshDefinition mesh = HumanoidModel.createMesh(g, 0.0F);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(5.0F, 2.0F, 0.0F));
        root.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, g), PartPose.offset(1.9F, 12.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
