# Auracast Companion
Android companion app for the Auracast Auto-Connection Assistant proof-of-concept. Automatically connects a rider's hearing device to the right stop announcement as they travel a public transport route.

This repository is the UI-facing portion. The transmitters are provisioned by the [FMA120 Provisioning Tool](https://github.com/yangzhie/fma120-provisioning-tool), whilst the auto-connection feature is powered by the (Auracast Auto-Connection Assistant)[https://github.com/yangzhie/auracast-ac-assistant], which directs the board firmware.
## Board Role
Joining an Auracast broadcast on someone else's behalf requires the **Broadcast Assistant** role, which on Android is gated behind `BLUETOOTH_PRIVILEGED` - a signature-level permission no third-party app can hold, regardless of SDK version or user consent.

So the application does everything it legally can, and hands the one restricted step (decides and instructs) to an nRF52840 running Zephyr.
## Application Role
1. **Scans** BLE advertisements for manufacturer data under company ID `0xFFFF` and decodes the 8-byte stop payload.
2. **Decides** which stop the rider is at, from the route position and RSSI.
3. **Instructs** the board over a custom 128-bit GATT service, and tracks the state it notifies back.
4. **Displays** journey progress (approaching, connecting, playing).
## Stop payload
8 bytes, carried in the transmitter's advertisement:
- `41 55` (`"AU"`): magic
- `01`: protocol version
- `56 00`: route ID (86), little-endian
- `01`: stop index
- `00`: direction (0 = outbound)
- `01`: language (1 = English)
## Build
```bash
./gradlew installDebug
```

Or open in Android Studio and run. Grant Nearby devices (`BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`) on first launch.
## Limitations
- **The board is required.** Without it the app discovers broadcasts and can do nothing with them. This is the finding the project exists to demonstrate, not a defect.
- **Consumer sinks implement BASS incompletely.** Both devices tested (JM320 earbuds, Sony WH-CH730N) fail to hand over cleanly on every stop change - the JM320 refuses receive-state reads outright. The architecture is sound; the sink firmware isn't there yet.
- **Fixed RSSI threshold** with no hysteresis, so the decision can flap near the boundary.
- **Demo route is hardcoded** in `SampleData.kt`.
