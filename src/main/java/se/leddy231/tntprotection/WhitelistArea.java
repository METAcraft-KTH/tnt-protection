package se.leddy231.tntprotection;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class WhitelistArea {

    public final String name;
    public final int fromX;
    public final int fromZ;
    public final int toX;
    public final int toZ;

    public WhitelistArea(String name, int x1, int z1, int x2, int z2) {
        this.name = name;
        this.fromX = Math.min(x1, x2);
        this.fromZ = Math.min(z1, z2);
        this.toX = Math.max(x1, x2);
        this.toZ = Math.max(z1, z2);
    }

    public boolean isInside(BlockPos pos) {
        int minX = fromX * 16;
        int minZ = fromZ * 16;
        int maxX = toX * 16 + 15;
        int maxZ = toZ * 16 + 15;
        int x = pos.getX();
        int z = pos.getZ();
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }

    public NbtCompound toNbt() {
        NbtCompound tag = new NbtCompound();
        tag.putString("name", name);
        tag.putInt("fromX", fromX);
        tag.putInt("fromZ", fromZ);
        tag.putInt("toX", toX);
        tag.putInt("toZ", toZ);
        return tag;
    }

    public static WhitelistArea fromNbt(NbtCompound tag) {
        String name = tag.getString("name");
        int fromX = tag.getInt("fromX");
        int fromZ = tag.getInt("fromZ");
        int toX = tag.getInt("toX");
        int toZ = tag.getInt("toZ");
        return new WhitelistArea(name, fromX, fromZ, toX, toZ);
    }

    public String toChat() {
        return "\"" + name + "\" from (x: " + fromX + ", z: " + fromZ + ") to (x: " + toX + ", z: " + toZ + ")";
    }

}
