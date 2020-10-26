# LyricsPrimer

## What's this

This project takes Japanese song lyrics as an input and splits the syllables (primarily by grouping latin letters to fit a plausible [kana](https://en.wikipedia.org/wiki/Kana)) for karaoke purposes. This allows us to paste `Zankoku na tenshi no teeze` and immediately get `Za/n/ko/ku na te/n/shi no te/e/ze` (ie. [exactly what should be used to tell people what to sing](https://youtu.be/nU21rCWkuJw?t=68))  
On top of this base idea we added some extra rules to correct unfit transcriptions and properly handle non letter characters, as well as some optional replacements for syllables that are often sung as a single one (eg. `Ge/k/ka bi/ji/n` -> `Gek/ka bi/jin`)

All of this is built as a single executable jar so you can run it under any Java supported system

## How to use

Run the application from your file explorer or with one of these commands:
- Using compiled jar
```bash
java -jar LyricsPrimer.jar
```
- From source
```bash
./gradlew run
```

![image](https://i.imgur.com/CCgr46X.png)

Once the application window is open, you'll see 2 big panels. Paste your lyrics in the left one, click the leftmost button and get your split text from the right panel.  
You can run the declared replacements with the middle button and the right button will remove any (text between parentheses) from the main output and append it as a 'Chorus' lyrics.

## How to build

I've added gradle to the project to ease the build process. Make sure you have an installed JDK (1.8 or later) that gradle can use and simply run
```bash
./gradlew build
```
The executable jar will be created under `build/libs`

[![Rod Fetalvero's DEV Profile](https://d2fltix0v2e0sb.cloudfront.net/dev-badge.svg)](https://dev.to/nyorja)
