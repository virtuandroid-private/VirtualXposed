<div align="center">

  # VirtualXposed

</div>

### Introduction

This is an updated version of [VirtualXposed](https://github.com/android-hacker/VirtualXposed). 
It is used to run multiple copies of any app and inject legacy Xposed modules on a stock Android device without root. 
This is possible due to the virtualization implementation [VirtualApp](https://github.com/asLody/VirtualApp) and the hooking
implementation by [Vector](https://github.com/JingMatrix/Vector).

This project is an implementation reference we use to evaluate attacks and defenses against virtualization frameworks.

---

### New features
- It supports Android 5-17
- It supports Xposed modules on x86_64 (emulators) via [LSPlant](https://github.com/LSPosed/LSPlant)
- Improved app permission handling
- Improved file sandboxing

---

### Building

The build process is much like a standard Android project, but with some small caveats:

1. This project uses submodules, therefore you will need to initialize those submodules after cloning.
2. You need Cmake *3.31.6* and *NDK 29.0.14206865*. These can be installed in Android studio by clicking **Tools > SDK Manager > SDK Tools > Show package details**
which show the specified version under **NDK (Side by side)** and **CMake**.

Begin by cloning the repository:

```sh
git clone https://github.com/virtuandroid-private/VirtualXposed-private
```

Initialize submodules:

```sh
git submodule update --init --recursive
```

You can now open the project in Android studio to build the project, by clicking the **Assemble** 
button using the **app** configuration. However, you can also build it manually using:

```sh
./gradlew build
```
