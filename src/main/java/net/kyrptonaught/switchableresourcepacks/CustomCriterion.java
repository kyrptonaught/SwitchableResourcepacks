package net.kyrptonaught.switchableresourcepacks;

import net.minecraft.advancement.criterion.ImpossibleCriterion;
import net.minecraft.util.Identifier;

public class CustomCriterion extends ImpossibleCriterion {

    public final Identifier id;

    public CustomCriterion(Identifier id) {
        this.id = id;
    }
}