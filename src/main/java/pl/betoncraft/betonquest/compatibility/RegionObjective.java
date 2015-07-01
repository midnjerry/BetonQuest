/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.compatibility;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.core.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Requires the player to enter the WorldGuard region
 * 
 * @author Jakub Sapalski
 */
public class RegionObjective extends Objective implements Listener {

    private final String name;
    private final WorldGuardPlugin worldGuard = (WorldGuardPlugin)
            Bukkit.getPluginManager().getPlugin("WorldGuard");
    
    /**
     * @param playerID
     * @param instructions
     * @throws InstructionParseException 
     */
    public RegionObjective(String playerID, String instructions)
            throws InstructionParseException {
        super(playerID, instructions);
        String[] parts = instructions.split(" ");
        if (parts.length < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        name = parts[1];
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!PlayerConverter.getID(event.getPlayer()).equals(playerID)) {
            return;
        }
        Location loc = event.getTo();
        RegionManager manager = worldGuard.getRegionManager(loc.getWorld());
        ProtectedRegion region = manager.getRegion(name);
        ApplicableRegionSet set = manager.getApplicableRegions(loc);
        for (ProtectedRegion compare : set) {
            if (compare.equals(region)) {
                if (checkConditions()) {
                    completeObjective();
                } else {
                    return;
                }
            }
        }
        
    }

    @Override
    public void delete() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getInstruction() {
        return instructions;
    }

}
