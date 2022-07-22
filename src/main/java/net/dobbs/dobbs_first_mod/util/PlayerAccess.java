package net.dobbs.dobbs_first_mod.util;

public interface PlayerAccess {
    boolean doesPlayerOwn(double x, double y, double z);
    void addTile(double x, double y, double z);

    void loadTileMap(String path);
}
