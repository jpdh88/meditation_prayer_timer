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
A Sequence will be composed of Primary Sequences and Secondary Sequences (each a length of time).
A Primary Sequence will always begin with the Primary Sound.
A Secondary Sequence will always begin with a Secondary Sound.
Each Sequence will have a maximum of two Primary Sequences. The first Primary Sequence will start the session and can be any length. The second Primary Sequence will end the session and will be of length 0 (really, it will be the length of the Primary Sound clip, but it won't have any independent duration)
Secondary Sequences are inserted between the two Primary Sequences.
