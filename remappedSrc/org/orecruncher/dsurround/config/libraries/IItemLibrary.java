package org.orecruncher.dsurround.config.libraries;

import org.orecruncher.dsurround.sound.ISoundFactory;

import java.util.Optional;
import net.minecraft.item.ItemStack;

public interface IItemLibrary extends ILibrary {

    Optional<ISoundFactory> getItemEquipSound(ItemStack stack);

    Optional<ISoundFactory> getItemSwingSound(ItemStack stack);

    Optional<ISoundFactory> getEquipableStepAccentSound(ItemStack stack);
}
