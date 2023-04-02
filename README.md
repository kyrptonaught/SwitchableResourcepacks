
# SwitchableResourcepacks

Server side mod adding a command to trigger a resourcepack to be sent to the client, using the same method as the built in required resourcepack function when joining a server. This mod will not do anything when installed on clients, and will function with vanilla clients.

#### This project has been merged into [Server Utils](https://github.com/kyrptonaught/Server-Utils). Further updates will occur there.

## How to use

This mod adds one command: 

    /loadresource <packname>
Executing this command will trigger the client to load a pack with the alias specified. `<packname>` being a config specified alias to a resourcepack. This can also be done from command blocks/MCF using `/execute as`

SwitchableResourcepacks also makes use of advancement criteria to inform about the status of the client loading the resourcepack. This can be used with datapacks to test if the player has unlocked the advancement, thus giving an update on the clients progress, or if it failed.
The included criteria :

 - `switchableresourcepacks:started` - Triggers when the client accepts the resourcepack and loading begins.
 - `switchableresourcepacks:finished` - Triggers when the resourcepack finished loading successfully.
 - `switchableresourcepacks:failed` - Triggers if the client fails to load the pack.

An example advancement is available [here](https://github.com/kyrptonaught/SwitchableResourcepacks/blob/main/example/exampleadvancement.json).

**Config**

Upon first start up, a basic config will be generated featuring an example pack setup. It will look like [so](https://github.com/kyrptonaught/SwitchableResourcepacks/blob/main/example/exampleconfig.json5). 

**Adding a new Pack**

Adding a new pack is as simple as adding a new entry inside of the config. See the example config above. 
		
    {
    
    "packname": "example_pack" - The alias used for the command to load this pack.
    
    "url": "https://example.com/resourcepack.zip", -Public url used to download the pack
    
    "hash": "examplehash", -The pack's SHA-1 hash. (Optional)
    
    "required": true, - Is the user required to use this pack? If the user declines they wull be kicked
    
    "hasPrompt": true, - Is the user prompted to use the pack? The prompt will always show if required is true
    
    "message": "plz use me" - Message to be displayed with the prompt

    }
The config also features `autoRevoke`. Enabling this will automatically revoke all of the above advancement criteria when another resourcepack is downloaded. If set to false, you must do the revoking on your own, else the criteria cannot be triggered again.


**This mod was a request from the wonderful folks behind the Legacy Edition Battle project**
Check them out [here](https://www.planetminecraft.com/project/legacy-edition-battle/), or their [github page](https://github.com/DBTDerpbox/Legacy-Edition-Battle).

