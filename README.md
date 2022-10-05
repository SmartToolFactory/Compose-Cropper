# Compose Image Cropper

[![](https://jitpack.io/v/SmartToolFactory/Compose-Cropper.svg)](https://jitpack.io/#SmartToolFactory/Compose-Cropper)


Image cropper that can crop with static, dynamic crop behavior, can use customizable shapes, vectors, and other png images as iamge to crop with various customizations.



https://user-images.githubusercontent.com/35650605/194116575-8a5cb314-a34d-433c-b2d2-c114253670ba.mp4



## Gradle Setup

To get a Git project into your build:

* Step 1. Add the JitPack repository to your build file Add it in your root build.gradle at the end
  of repositories:

```
allprojects {
  repositories {
      ...
      maven { url 'https://jitpack.io' }
  }
}
```

* Step 2. Add the dependency

```
dependencies {
	  implementation 'com.github.SmartToolFactory:Compose-Cropper:Tag'
}
```

## ⚠️ This version is for testing for features
If you find any bugs please open an issue, suggest a solution or fork and open a PR. It's more than welcome to assiting for finding bugs or things that don't work as intended faster.

## Features
* Crop with Static Overlay
* Animate image back to bounds
* Crop with Dynamic resizable overlay
* Animate overlay back to bounds
* Animate image to overlay bounds
* Crop with shapes including Rectangle, Rounded Rectangle, Cut Corner Shape, Oval, and Polygons
* Crop with vector drawables
* Crop with png files as image mask
* Option to change Content scale from 7 options
* Option to change fling gesture when pointer is up to continue movement
* Overlay and grid color, stroke width options

This library uses

[Colorful Sliders](https://github.com/SmartToolFactory/Compose-Colorful-Sliders)

Colorful Sliders written with Jetpack Compose that enliven default sliders with track and thumb
dimensions, and gradient colors, borders, labels on top or at the bottom move with thumb and
ColorfulIconSlider that can display emoji or any Composable as thumb

[Color Picker Bundle](https://github.com/SmartToolFactory/Compose-Color-Picker-Bundle)

Collection of Color Pickers written with Jetpack Compose with solid Color or Gradient with type,
tile mode, and color stops in HSL/HSV/RGB models with Colorful Sliders, displays, and many
customization options.

[Extended Gestures](https://github.com/SmartToolFactory/Compose-Extended-Gestures)

Jetpack Compose gesture library that expands available gesture functions with onTouchEvent
counterpart of event, transform and touch delegate gestures.


