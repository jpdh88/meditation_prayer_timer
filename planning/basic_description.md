# Meditation and Prayer Timer
A very simple app to time meditation and prayer sessions, particularly sessions
that might involve several stages where each equire a tone or bell.

E.g. you might want to do technique x for 20 minutes, then begin technique y for
9 minutes. Technique y might require a tone or bell every 3 minutes.

## Examples of sessions

### Simplest:
 O----------O       where O = primary bell tone, - = one minute

### Two stages:
 O-----o-----O      o = secondary bell tone

### Four stages:
 O--------------------o---o---o---O

### Crazy:
 O---o-----o--o--------o-----------o--o-o--o-O
 (but this should be possible)

## Basic Plan

A session will be represented by a Sequence (a length of time).
A Sequence will be composed of Intervals.
An Interval is composed of a sound and a duration. A sound is played and the duration timer begins.
The first Interval in the sequence must use primary sound; the last Interal in the sequence must use Primary sound and be of duration 0 (actually, duration of Primary sound)
Intervals in between the first and last Intervals use Secondary sound and have a duration > 0.

## Glossary/More Info.
A meditation/prayer session is represented by a Sequence.
Sequence    = a series of SubSequences
            = two Sound Objects (one for Main Intervals and one for Secondary Intervals)
Interval = a duration
            = a "type":
                - a first or last SubSequences (a "Main" Interval)
                - an intervening Interval
                + there are two types b/c each type has a different sound associated w/ it
Sounds: are 15s exactly (to make things easier with the timer)
