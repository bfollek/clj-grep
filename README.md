# clj-grep

Lightweight grep based on an exercism.io python exercise.

## Usage

```
clj-grep.core=> (grep "ACHILLES" "-i" ["iliad.txt"])
"Achilles sing, O Goddess! Peleus' son;\nThe noble Chief Achilles from the son\n"

clj-grep.core=> cli-options
[["-x" "--entire-lines" :default false]
 ["-i" "--ignore-case" :default false]
 ["-v" "--invert" :default false]
 ["-n" "--line-numbers" :default false]
 ["-l" "--only-names" :default false]]
```

## License

Copyright © 2019 Bob Follek

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
