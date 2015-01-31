# BoundingBoxOutlineReloaded

BoundingBoxOutlineReloaded is a mod for Minecraft 1.8 using Forge API.

# Why did I make it?

I loved 4poc's BBOutline mod but the only version I could get to work consistently was for Minecraft 1.6.4. This is fine if you want Nether Fortress bounding boxes but if you need witch huts the new block types can cause Minecraft 1.6.4 to crash horribly; and don't get me started on item frames crashing Minecraft 1.6.4!

In addition to this not working with newer worlds, the way it bounds villages lacks the finesse of KaboPC's VillageMarker mod, and any new structures introduced in Minecraft like Ocean Monuments are missing entirely.

# What it does

This mod highlights in a variety of colours and styles the different structures & features of the game:-
- Nether Fortresses; red boxes bound each individual area where Blaze, Wither Skeletons & normal Skeletons will spawn. Time for a beacon methinks!
- Witch Huts; blue boxes reveal everywhere only witches spawn. Witch farm anyone?
- Desert Temples; orange boxes envelop the pyramid and towers. Go grab some loot but beware TNT boobie traps!
- Jungle Temples; green boxes surround the temple. Indianna Jones would've love these!
- Ocean Monuments; cyan boxes indicate where guardians spawn. New sea lantern and prismarine block types FTW.
- Strongholds; yellow boxes show each room in the stronghold. Does anyone make anything with silverfish spawners?
- Mine Shafts; light gray boxes illustrate each of the mine shafts. Cobwebs... grrr!
- Villages; multicoloured spheres encircle  the village, with boxes marking if and where iron golems will spawn. You should see the iron titan... CRAZY!
- Slime chunks; bright green boxes highlight where slimes will spawn, with a dynamic box that rises to where the players feet are to help find them from the surface. Bouncy... bouncy...

# How it works

As chunks are loaded the game provides metadata about all the different structures & features in those chunks.  The mod interprets this meta data, caches the results, and renders the bounding boxes to the screen.  In an SMP environment this data is not present on the clients so the mod needs to run on the server where the processing happens and then the relevant metadata is sent to the clients for them to render.

To toggle the rendering of the bounding boxes press B.

# Installing

Make sure you have Forge 1.8 installed then drop the jar file into the mods/1.8 folder.  Remember this will need to be installed on client and server in an SMP scenario.

# Using

Press B, sit back and enjoy the goodness flowing onto your screen.

# Configuring

The keyboard shortcut can be configured in the standard Controls screen.

There are two ways to edit the config - in game or cfg file editing (although cfg editing is the only option on SMP servers.)

The following options are available for configuration

Option | Client/Server | Description | Cfg File Key | Cfg File Values | Default
--- | --- | --- | --- | --- | ---
Nether Fortresses | Both | Process/Render Nether Fortresses | drawDesertTemples | true/false | true
Witch Huts | Both | Process/Render Witch Huts | drawWitchHuts | true/false | true
Desert Temples | Both | Process/Render Desert Temples | drawDesertTemples | true/false | true
Jungle Temples | Both | Process/Render Jungle Temples | drawJungleTemples | true/false | true
Ocean Monuments | Both | Process/Render Ocean Monuments | drawOceanMonuments | true/false | true
Strongholds | Both | Process/Render Strongholds | drawStrongholds | true/false | false
Mine Shafts | Both | Process/Render Mine Shafts | drawMineShafts | true/false | false
Villages | Both | Process/Render Villages | drawVillages | true/false | true
Village spheres | Client | Render Villages as spheres instead of cuboids | renderVillageAsSphere | true/false | true
Village Iron Golem Spawn Area | Client | Render Iron Golem Spawn Area within valid Villages | drawIronGolemSpawnArea | true/false | true
Slime Chunks | Both | Process/Render Slime Chunks | drawSlimeChunks | true/false | true
Slime Chunks Maximum Y | Client | Maximum Y value of the dynamic slime chunk boxes | slimeChunkMaxY | 0/40-255 (0 = no limit) | 0
Fill | Client | Fill the bounding boxes (except village ones) | fill | true/false | false
Always Visible | Client | Bounding boxes are visible through blocks - kinda messes with perspective tho! | alwaysVisible | true/false | false

### In game

*Note: In game editing is incomplete.*

Forge provides a UI to edit configuration of mods - this mod provides the ability to control many aspects of the mods behaviour.  On the home screen of Minecraft select the Mods button, select this mod and click on the config button.

### Cfg file editing

Open the config/BBOutlineReloaded.cfg file with your text editor of choice and change the settings.  Simples!  Minecraft (including servers) will need to be restarted for the settings to take effect.

# Links
- Forge 1.8 - [Download](http://files.minecraftforge.net/minecraftforge/1.8)
- 4poc's BBOutline mod - [Forum](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1286555-bounding-box-outline) | [Source](http://www.github.com/4poc/bboutline)
- KaboPC's VillageMarker mod - [Forum](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/1288327-village-marker-mod)
