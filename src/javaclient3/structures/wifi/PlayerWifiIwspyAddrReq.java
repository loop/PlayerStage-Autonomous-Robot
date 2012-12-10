/*
 *  Player Java Client 3 - PlayerWifiIwspyAddrReq.java
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
 * $Id: PlayerWifiIwspyAddrReq.java 125 2011-03-24 02:24:05Z corot $
 *
 */

package javaclient3.structures.wifi;

import javaclient3.structures.*;

/**
 * Request/reply:
 * @author Radu Bogdan Rusu
 * @version
 * <ul>
 *      <li>v3.0 - Player 3.0 supported
 * </ul>
 */
public class PlayerWifiIwspyAddrReq implements PlayerConstants {

    // Address
    private char[] address = new char[32];


    /**
     * @return  Address
     */
    public synchronized char[] getAddress () {
        return this.address;
    }

    /**
     * @param newAddress  Address
     */
    public synchronized void setAddress (char[] newAddress) {
        this.address = newAddress;
    }
}