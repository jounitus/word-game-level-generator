# WORD GAME LEVEL GENERATOR



## The Idea

The original idea was to make a level generator that can create
levels like in the game 
[Wordscape](https://play.google.com/store/apps/details?id=com.peoplefun.wordcross&hl=en).

The generator has a list of words that can be built from a specific set
of unique letters. For example with the letters **E**, **L** and **S**, 
you can get words **ELSE**, **SELL**, **SEES** and **LESS**.

These words are then plotted to the crosswords -style matrix of letters.

I used [Clojure](https://clojure.org/) to build the level generator and the 
functional programming style of Clojure did fit this project perfectly.

## Building

* Use can use [Lein](https://leiningen.org/) to run and build the game 
(server.clj is the entry point)
* You can also use Docker to build the server image 


## Previewing

Once you have the server running, there is:

* `/v1/generate-level` url to see the randomly generated levels (json)
* `/static/test.html` url to see HTML version of the generated levels

## Live Version

* JSON generated: http://word-game-level-generator.jounitus.com/v1/generate-level
* HTML level preview: http://word-game-level-generator.jounitus.com/static/test.html

## Word Database Used

Currently, the generator uses 
["google-10000-english-no-swears.txt"](https://github.com/first20hours/google-10000-english/blob/master/google-10000-english-no-swears.txt)
word list compiled by [Josh Kaufman](https://github.com/first20hours)

## TODO

* Try out different combinations of words when building levels and use the best one
* When generating levels, make sure that the level has more than
X number of words plotted on it or use a different set of words
* Add REST endpoint to see the words with the most combinations
* Use a word dictionary with more words (but prioritize more popular words) 
* Add support for (regex) blacklist of words
* Add command line interface to display words etc
* Make an actual game that uses it



