/*
 *  Player Java Client 3 - PlayerLimbGeomReq.java
 *  Copyright (C) 2006 Radu Bogdan Rusu
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: PlayerLimbGeomReq.java 125 2011-03-24 02:24:05Z corot $
 *
 */

package javaclient3.structures.limb;

import javaclient3.structures.*;

/**
 * Request/reply: get geometry
 * Query geometry by sending a null PLAYER_LIMB_GEOM_REQ reqest.
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerLimbGeomReq implements PlayerConstants {

    // The base position of the end-effector in robot coordinates.
    private PlayerPoint3d basePos;

    /**
     * @return  The base position of the end-effector in robot coordinates.
     */
    public synchronized PlayerPoint3d getX () {
        return this.basePos;
    }

    /**
     * @param newBasePos The base position of the end-effector in robot coordinates.
     */
    public synchronized void setBasePos (PlayerPoint3d newBasePos) {
        this.basePos = newBasePos;
    }
}