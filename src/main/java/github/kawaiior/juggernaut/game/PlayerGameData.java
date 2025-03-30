package github.kawaiior.juggernaut.game;

public class PlayerGameData {

    private boolean juggernaut = false;
    private int jKillCount = 0;
    private int killCount = 0;
    private int deathCount = 0;
    private float damageAmount = 0;
    private float bearDamage = 0;

    public boolean isJuggernaut() {
        return juggernaut;
    }

    public void setJuggernaut(boolean juggernaut) {
        this.juggernaut = juggernaut;
    }

    public void causeDamage(float amount) {
        this.damageAmount += amount;
    }

    public void playerBearDamage(float amount) {
        this.bearDamage += amount;
    }

    public void killJuggernaut() {
        this.jKillCount++;
    }

    public void killPlayer() {
        this.killCount++;
    }

    public void playerDeath() {
        this.deathCount++;
    }

    public int getJKillCount() {
        return jKillCount;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public float getDamageAmount() {
        return damageAmount;
    }

    public float getBearDamage() {
        return bearDamage;
    }

    public void reset() {
        this.juggernaut = false;
        this.jKillCount = 0;
        this.killCount = 0;
        this.deathCount = 0;
        this.damageAmount = 0;
        this.bearDamage = 0;
    }
}
