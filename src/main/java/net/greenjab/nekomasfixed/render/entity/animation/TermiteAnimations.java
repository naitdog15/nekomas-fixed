package net.greenjab.nekomasfixed.render.entity.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class TermiteAnimations {
    public static final AnimationDefinition ANIM_TERMITE_SWIPE = AnimationDefinition.Builder.withLength(1F)
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("pincher", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("pincher", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F, KeyframeAnimations.posVec(0F, 0F, -0.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("pincher", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.25F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 2.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
            )).build();

    /** Two bites a cycle: the pincher opens and snaps, the head rocks into the log, the abdomen breathes. */
    public static final AnimationDefinition ANIM_TERMITE_CHEW = AnimationDefinition.Builder.withLength(0.56F)
            .looping()
            .addAnimation("pincher", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.00F, KeyframeAnimations.scaleVec(1.00F, 1.00F, 1.00F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.07F, KeyframeAnimations.scaleVec(1.16F, 1.00F, 1.00F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.scaleVec(0.84F, 1.00F, 1.00F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.28F, KeyframeAnimations.scaleVec(1.00F, 1.00F, 1.00F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.35F, KeyframeAnimations.scaleVec(1.16F, 1.00F, 1.00F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.42F, KeyframeAnimations.scaleVec(0.84F, 1.00F, 1.00F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.56F, KeyframeAnimations.scaleVec(1.00F, 1.00F, 1.00F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("pincher", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.00F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.07F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.28F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.35F, KeyframeAnimations.degreeVec(8.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(-4.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.56F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.00F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.07F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(3.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.28F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.35F, KeyframeAnimations.degreeVec(-1.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(3.5F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.56F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.00F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.00F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.07F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.04F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.14F, KeyframeAnimations.posVec(0.0F, 0.0F, -0.20F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.28F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.00F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.35F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.04F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.42F, KeyframeAnimations.posVec(0.0F, 0.0F, -0.20F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.56F, KeyframeAnimations.posVec(0.0F, 0.0F, 0.00F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("antler", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.00F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.07F, KeyframeAnimations.degreeVec(0.0F, 4.0F, -3.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.14F, KeyframeAnimations.degreeVec(0.0F, -3.0F, 2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.28F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.35F, KeyframeAnimations.degreeVec(0.0F, -4.0F, 3.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.42F, KeyframeAnimations.degreeVec(0.0F, 3.0F, -2.0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.56F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("sack", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.00F, KeyframeAnimations.scaleVec(1.000F, 1.000F, 1.000F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.28F, KeyframeAnimations.scaleVec(1.025F, 1.020F, 1.030F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.56F, KeyframeAnimations.scaleVec(1.000F, 1.000F, 1.000F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .build();
}