package ca.duffyco.RenderEngine;

import java.util.Iterator;

/**
 * Created by jon on 10/26/2016.
 */

public class SequentialCoordinatedEventGroup<T> extends CoordinatedEventGroup<T> {
        protected Iterator<Effect> effectIter = null;

        @Override
        protected boolean subDraw(float fpsRatio) {
            if( effectIter == null ) {
                effectIter = (Iterator<Effect>) effects.iterator();
                currentEffect = effectIter.next();
            }

            if( !currentEffect.isComplete() ) {
                currentEffect.draw( fpsRatio );
            }
            else if( effectIter.hasNext() )
            {
                currentEffect = effectIter.next();
                currentEffect.setActive( true );
                currentEffect.draw( fpsRatio );
            }
            else
                return false;

            return true;
        }

        @Override
        public int getTextureID( int i ){
            if( i < currentEffect.getTextureID() )
                return i;

            return getTextureID();
        }

        public void reset() {
            super.reset();
            effectIter = null;
        }
}
