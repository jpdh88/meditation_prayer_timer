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
A Sequence will be composed of SubSequences.
A SubSequence is composed of a sound and a duration. A sound is played and the duration timer begins.
The first SubSequence in the sequence must use primary sound; the last Subsequence in the sequence must use Primary sound and be of duration 0 (really, duration of Primary sound)
SubSequences in between the first and last SubSequences use Secondary sound and have a duration > 0.