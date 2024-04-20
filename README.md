![RemoteGarden](src/main/java/edu/redwoods/cis18/scam/projectlogo.jpg "Soil Moisture Sensor")

## Overview
Welcome to the RemoteGarden, where we strive to digitally greenify your thumb! This project utilizes an Arduino Uno R3 to monitor soil moisture and reports it back to a Java-based backend—because even plants have gone digital.

### What It Does
The system reads moisture levels from a soil sensor and sends this data through the mystical ethers of USB to a Java application. This data can eventually be saved into a database.

### Current Capabilities
- **Arduino Readings**: Every second, our trusty Arduino reads how thirsty your plant is and sends this data serially.
- **Java Application**: Receives this data and contemplates what life means when you're a byte flowing through copper.
- **JavaFX Interface**: loads 

## Tier 1
- **Database Integration**: Data needs to go somewhere more permanent than a volatile StringBuilder object.
- **Data Processing**: Replace the primitive `processLine` method to do something more database-y.
- **Refactor**: `CheckBoxListCell` needs to complete de-nesting from `Main.java`.
- **JavaFX**: GUI Slides incoming.

## Tier 2
- **Arduino Wifi Integration**: Because wires are so 1990s.
- **Additional Sensors**: Why not make it more complicated?

## Installation
Just clone this baby and have Maven and JDK 22 ready. Run `mvn clean compile javafx:run` and pray to the coding gods the dependencies remain unfucked.

## Contributions
Feel free to fork, clone, and tinker. Pull requests that make the plants happier are always welcome. If you break it - congratulations! You now own both pieces.
**PRIORITY**: Migration from [Remote-Garden-Project](https://github.com/Miaka2/Remote-Garden-Project) may not yet be complete. Prioritize migration over modification of your code until migration is complete and the [first project](https://github.com/Miaka2/Remote-Garden-Project "Optional title") can be properly abandoned.

## Known Issues
- aint got no database
- Warnings about digits in module names – apparently Java thinks that problematic. Consider renaming cis18/ dir if necessary.
- Unused setters in `MoistureAlertSystem` – probably will remain unused until JavaFX sliders in the GUI are coded to call setters. 
- Unchecked generics – because who really checks those anyway?
- gui ugly af

Happy coding. 🌱
