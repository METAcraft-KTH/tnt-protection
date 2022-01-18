package se.leddy231.tntprotection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

public class ProtectionStateManager extends PersistentState{

    private static final String stateManagerKey = "tnt-protection";
    private static final String areasNbtTag = "areas";

    public static ProtectionStateManager instance;

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            TntProtection.LOGGER.info("Loading state manager");
            ProtectionStateManager.instance = server.getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(ProtectionStateManager::fromNbt, ProtectionStateManager::new, stateManagerKey);
            TntProtection.LOGGER.info(instance);
        });
    }

    private List<WhitelistArea> areas;

    public ProtectionStateManager() {
        areas = new ArrayList<>();
    }

    public void addArea(WhitelistArea area) {
        areas.add(area);
        markDirty();
    }

    public boolean removeArea(String name) {
        for (int i = 0; i < areas.size(); i++) {
            WhitelistArea area = areas.get(i);
            if (area.name.equals(name)) {
                areas.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean isWhitelisted(BlockPos pos, World world) {
        if (!world.getRegistryKey().equals(World.OVERWORLD)) {
            return false;
        }
        for (WhitelistArea area : areas) {
            if (area.isInside(pos)) {
                return true;
            }
        }
        return false;
    }

    public List<WhitelistArea> getAreas() {
        return Collections.unmodifiableList(areas);
    }

    public static ProtectionStateManager fromNbt(NbtCompound tag) {
        ProtectionStateManager manager = new ProtectionStateManager();
        manager.readNbt(tag);
        return manager;
    }

    public void readNbt(NbtCompound tag) {
        if (tag.contains(areasNbtTag)) {
            NbtList nbtList = tag.getList(areasNbtTag, 10);
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                areas.add(WhitelistArea.fromNbt(nbtCompound));
            }
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        NbtList list = new NbtList();
        for (WhitelistArea area : areas) {
            list.add(area.toNbt());
        }
        tag.put(areasNbtTag, list);
        return tag;
    }
    
}
