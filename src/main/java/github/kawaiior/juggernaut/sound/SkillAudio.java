package github.kawaiior.juggernaut.sound;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SkillAudio extends TickableSound {
    public SkillAudio(SoundEvent soundEvent, SoundCategory soundCategory) {
        super(soundEvent, soundCategory);
        this.attenuationType = AttenuationType.NONE;
    }

    @Override
    public void tick() {

    }

}
