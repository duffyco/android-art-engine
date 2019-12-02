package ca.duffyco.RenderEngine;

import java.util.ArrayList;

/**
 * Created by lidia on 2/21/2016.
 */
abstract public class Piece {

    protected ArrayList<Effect<?>> effects = new ArrayList<Effect<?>>();
    protected ArrayList<Texture> textures = new ArrayList<Texture>();

    public ArrayList<Texture> getTextures() {
        return textures;
    }

    public ArrayList<Effect<?>> getEffects() {
        return effects;
    }

    abstract public void setup();
}
