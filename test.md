# Guide de test complet du debugger JDI

## Setup
- Classe debuggée : `JDIComplexDebuggee`
- Point d'entrée : `main()` ligne 106
- Le debugger s'arrête automatiquement au début du main

---

## Phase 1 : Démarrage et inspection initiale

### Commande : `help`
**Résultat attendu :**
```
JAVA Debugger help menu
-----------------------
help : Affiche l'aide du debugger
step : Mode pas à pas détaillé
step-over : Mode pas à pas
continue : Reprendre l'execution jusqu'au prochain point d'arrêt
frame : Affiche la frame courante
temporaries : Affiche les variables temporaires de la frame courante
stack : Affiche la pile d'appel
receiver : Affiche le receveur de la méthode courante (this)
sender : Affiche l'objet qui a appelé la méthode courante
receiver-variables : Affiche les variables d'instance du receveur courant
method : Affiche la méthode en cours d'exécution
arguments : Affiche les arguments de la méthode courante
print-var : Imprime la valeur d'une variable (usage: print-var <varName>)
break : Installe un point d'arrêt (usage: break <filename> <lineNumber>)
breakpoints : Liste les points d'arrêt actifs
break-once : Installe un point d'arrêt à usage unique (usage: break-once <filename> <lineNumber>)
break-on-count : Installe un point d'arrêt conditionnel (usage: break-on-count <filename> <lineNumber> <count>)
break-before-method-call : S'arrête au début de l'exécution d'une méthode (usage: break-before-method-call <methodName>)
```

### Commande : `frame`
**Résultat attendu :**
```
Current frame:
  Method: main
  Class: JDIComplexDebuggee
  Line: 106
  Source: JDIComplexDebuggee.java
```

### Commande : `method`
**Résultat attendu :**
```
Current method:
  Name: main
  Signature: ([Ljava/lang/String;)V
  Declaring type: JDIComplexDebuggee
  Return type: void
  Is static: true
  Is constructor: false
```

### Commande : `arguments`
**Résultat attendu :**
```
Method arguments:
  args → Array[0]
```

### Commande : `stack`
**Résultat attendu :**
```
Call stack:
  #0 JDIComplexDebuggee.main() line 106
```

---

## Phase 2 : Navigation avec step-over

### Commande : `step-over`
**Position attendue :** Ligne 109 `JDIComplexDebuggee calc = new JDIComplexDebuggee();`
**Résultat attendu :**
```
Step over enabled
```

### Commande : `step-over`
**Position attendue :** Ligne 112 `int sum1 = calc.add(5, 3);`

### Commande : `temporaries`
**Résultat attendu :**
```
Temporary variables:
  args → Array[0]
  calc → JDIComplexDebuggee@<uniqueID>
```

### Commande : `print-var calc`
**Résultat attendu :**
```
calc = JDIComplexDebuggee@<uniqueID>
```

### Commande : `print-var args`
**Résultat attendu :**
```
args = Array[0]
```

---

## Phase 3 : Step-into dans une méthode (add)

### Commande : `step`
**Position attendue :** Ligne 18 (première ligne de `add(int a, int b)`)
**Résultat attendu :**
```
Step into enabled
```

### Commande : `frame`
**Résultat attendu :**
```
Current frame:
  Method: add
  Class: JDIComplexDebuggee
  Line: 18
  Source: JDIComplexDebuggee.java
```

### Commande : `method`
**Résultat attendu :**
```
Current method:
  Name: add
  Signature: (II)I
  Declaring type: JDIComplexDebuggee
  Return type: int
  Is static: false
  Is constructor: false
```

### Commande : `arguments`
**Résultat attendu :**
```
Method arguments:
  a → 5
  b → 3
```

### Commande : `receiver`
**Résultat attendu :**
```
Receiver (this): JDIComplexDebuggee@<uniqueID>
```

### Commande : `sender`
**Résultat attendu :**
```
Sender: static context or no 'this' in caller
```
*(car appelé depuis main qui est statique)*

### Commande : `stack`
**Résultat attendu :**
```
Call stack:
  #0 JDIComplexDebuggee.add() line 18
  #1 JDIComplexDebuggee.main() line 112
```

### Commande : `step-over`
**Position attendue :** Ligne 19 `String operation = "addition";`

### Commande : `temporaries`
**Résultat attendu :**
```
Temporary variables:
  a → 5
  b → 3
  sum → 8
```

### Commande : `print-var sum`
**Résultat attendu :**
```
sum = 8
```

### Commande : `print-var a`
**Résultat attendu :**
```
a = 5
```

### Commande : `print-var operation`
**Résultat attendu :**
```
Variable 'operation' not found
```
*(pas encore déclarée à cette ligne)*

### Commande : `step-over`
**Position attendue :** Ligne 21 `this.result = sum;`

### Commande : `receiver-variables`
**Résultat attendu :**
```
Receiver instance variables:
  result → 0
  lastOperation → "none"
  operationCount → 0
```

### Commande : `step-over`
**Position attendue :** Ligne 22

### Commande : `step-over`
**Position attendue :** Ligne 23

### Commande : `step-over`
**Position attendue :** Ligne 25

### Commande : `receiver-variables`
**Résultat attendu :**
```
Receiver instance variables:
  result → 8
  lastOperation → "addition"
  operationCount → 1
```

---

## Phase 4 : Breakpoint simple et continue

### Commande : `break JDIComplexDebuggee.java 62`
**Résultat attendu :**
```
Breakpoint set at JDIComplexDebuggee.java:62
```

### Commande : `breakpoints`
**Résultat attendu :**
```
Active breakpoints:
  #1 JDIComplexDebuggee.java:62 (JDIComplexDebuggee.factorial)
```

### Commande : `continue`
**Position attendue :** Ligne 62 dans `factorial()` (première fois, avec n=5)

### Commande : `frame`
**Résultat attendu :**
```
Current frame:
  Method: factorial
  Class: JDIComplexDebuggee
  Line: 62
  Source: JDIComplexDebuggee.java
```

### Commande : `arguments`
**Résultat attendu :**
```
Method arguments:
  n → 5
```

### Commande : `print-var n`
**Résultat attendu :**
```
n = 5
```

### Commande : `continue`
**Position attendue :** Ligne 62 (deuxième appel récursif, n=4)

### Commande : `arguments`
**Résultat attendu :**
```
Method arguments:
  n → 4
```

### Commande : `stack`
**Résultat attendu :**
```
Call stack:
  #0 JDIComplexDebuggee.factorial() line 62
  #1 JDIComplexDebuggee.factorial() line 66
  #2 JDIComplexDebuggee.main() line 103
```

### Commande : `continue`
**Position attendue :** Ligne 62 (n=3)

### Commande : `stack`
**Résultat attendu :**
```
Call stack:
  #0 JDIComplexDebuggee.factorial() line 62
  #1 JDIComplexDebuggee.factorial() line 66
  #2 JDIComplexDebuggee.factorial() line 66
  #3 JDIComplexDebuggee.main() line 103
```

### Commande : `arguments`
**Résultat attendu :**
```
Method arguments:
  n → 3
```

### Commande : `temporaries`
**Résultat attendu :**
```
Temporary variables:
  n → 3
```

---

## Phase 5 : Break-on-count (breakpoint conditionnel)

**Note :** Redémarrer le debugger pour cette phase

### Commande : `break-on-count JDIComplexDebuggee.java 18 3`
**Résultat attendu :**
```
Count-based breakpoint set at JDIComplexDebuggee.java:18 (will break after 3 hits)
```

### Commande : `breakpoints`
**Résultat attendu :**
```
Active breakpoints:
  #1 JDIComplexDebuggee.java:18 (JDIComplexDebuggee.add)
```

### Commande : `continue`
**Position attendue :** Ligne 18, au 3ème appel de `add()` (probablement `add(100, 200)` ligne 118)

### Commande : `arguments`
**Résultat attendu :**
```
Method arguments:
  a → 100
  b → 200
```

### Commande : `receiver-variables`
**Résultat attendu :**
```
Receiver instance variables:
  result → <valeur de l'opération précédente>
  lastOperation → <opération précédente>
  operationCount → 2
```

### Commande : `stack`
**Résultat attendu :**
```
Call stack:
  #0 JDIComplexDebuggee.add() line 18
  #1 JDIComplexDebuggee.main() line 118
```

---

## Phase 6 : Break-once (breakpoint unique)

**Note :** Redémarrer le debugger pour cette phase

### Commande : `break-once JDIComplexDebuggee.java 33`
**Résultat attendu :**
```
One-time breakpoint set at JDIComplexDebuggee.java:33
```

### Commande : `breakpoints`
**Résultat attendu :**
```
Active breakpoints:
  #1 JDIComplexDebuggee.java:33 (JDIComplexDebuggee.multiply)
```

### Commande : `continue`
**Position attendue :** Ligne 33 dans `multiply()`

### Commande : `frame`
**Résultat attendu :**
```
Current frame:
  Method: multiply
  Class: JDIComplexDebuggee
  Line: 33
  Source: JDIComplexDebuggee.java
```

### Commande : `arguments`
**Résultat attendu :**
```
Method arguments:
  a → 4
  b → 7
```

### Commande : `continue`
**Résultat attendu :** Le programme continue jusqu'à la fin (le breakpoint ne se redéclenche pas)

### Commande : `breakpoints`
**Résultat attendu :**
```
No active breakpoints
```
*(le breakpoint one-time a été automatiquement supprimé)*

---

## Phase 7 : Break-before-method-call

**Note :** Redémarrer le debugger pour cette phase

### Commande : `break-before-method-call safeDivide`
**Résultat attendu :**
```
Will break on entry to JDIComplexDebuggee.safeDivide
Method entry breakpoint enabled for: safeDivide
```

### Commande : `continue`
**Position attendue :** Ligne 54 (première ligne de `safeDivide`)

### Commande : `frame`
**Résultat attendu :**
```
Current frame:
  Method: safeDivide
  Class: JDIComplexDebuggee
  Line: 54
  Source: JDIComplexDebuggee.java
```

### Commande : `arguments`
**Résultat attendu :**
```
Method arguments:
  a → 20
  b → 4
```

### Commande : `stack`
**Résultat attendu :**
```
Call stack:
  #0 JDIComplexDebuggee.safeDivide() line 54
  #1 JDIComplexDebuggee.divide() line 42
  #2 JDIComplexDebuggee.main() line 100
```

### Commande : `sender`
**Résultat attendu :**
```
Sender: JDIComplexDebuggee@<uniqueID>
```
*(l'instance calc qui appelle depuis divide)*

### Commande : `step-over`
**Position attendue :** Ligne 55 `if (b == 0)`

### Commande : `print-var b`
**Résultat attendu :**
```
b = 4
```

### Commande : `continue`
**Position attendue :** Ligne 54 (deuxième appel avec division par 0)

### Commande : `arguments`
**Résultat attendu :**
```
Method arguments:
  a → 10
  b → 0
```

### Commande : `step-over`
**Position attendue :** Ligne 55

### Commande : `step-over`
**Position attendue :** Ligne 56 (dans le bloc if, car b == 0)

### Commande : `temporaries`
**Résultat attendu :**
```
Temporary variables:
  a → 10
  b → 0
```

---

## Phase 8 : Navigation dans processArray

**Note :** Redémarrer le debugger pour cette phase

### Commande : `break JDIComplexDebuggee.java 46`
**Résultat attendu :**
```
Breakpoint set at JDIComplexDebuggee.java:46
```

### Commande : `continue`
**Position attendue :** Ligne 46 (dans la boucle for de `processArray`)

### Commande : `frame`
**Résultat attendu :**
```
Current frame:
  Method: processArray
  Class: JDIComplexDebuggee
  Line: 46
  Source: JDIComplexDebuggee.java
```

### Commande : `arguments`
**Résultat attendu :**
```
Method arguments:
  numbers → Array[5]
```

### Commande : `temporaries`
**Résultat attendu :**
```
Temporary variables:
  numbers → Array[5]
  total → 0
  max → -2147483648
  i → 0
  current → 1
```

### Commande : `print-var numbers`
**Résultat attendu :**
```
numbers = Array[5]
```

### Commande : `print-var i`
**Résultat attendu :**
```
i = 0
```

### Commande : `print-var current`
**Résultat attendu :**
```
current = 1
```

### Commande : `step-over`
**Position attendue :** Ligne 47

### Commande : `step-over`
**Position attendue :** Ligne 49

### Commande : `step-over`
**Position attendue :** Ligne 50

### Commande : `print-var total`
**Résultat attendu :**
```
total = 1
```

### Commande : `print-var max`
**Résultat attendu :**
```
max = 1
```

### Commande : `continue`
**Position attendue :** Ligne 46 (itération suivante, i=1)

### Commande : `print-var i`
**Résultat attendu :**
```
i = 1
```

### Commande : `print-var current`
**Résultat attendu :**
```
current = 2
```

### Commande : `continue`
**Position attendue :** Ligne 46 (itération suivante, i=2)

### Commande : `temporaries`
**Résultat attendu :**
```
Temporary variables:
  numbers → Array[5]
  total → 3
  max → 2
  i → 2
  current → 3
```

---

## Phase 9 : Test de print-var sur variable inexistante

### Commande : `print-var invalidVar`
**Résultat attendu :**
```
Variable 'invalidVar' not found
```

### Commande : `print-var total`
**Résultat attendu :**
```
total = 3
```

---

## Phase 10 : Multiple breakpoints

**Note :** Redémarrer le debugger pour cette phase

### Commande : `break JDIComplexDebuggee.java 18`
**Résultat attendu :**
```
Breakpoint set at JDIComplexDebuggee.java:18
```

### Commande : `break JDIComplexDebuggee.java 33`
**Résultat attendu :**
```
Breakpoint set at JDIComplexDebuggee.java:33
```

### Commande : `break JDIComplexDebuggee.java 62`
**Résultat attendu :**
```
Breakpoint set at JDIComplexDebuggee.java:62
```

### Commande : `breakpoints`
**Résultat attendu :**
```
Active breakpoints:
  #1 JDIComplexDebuggee.java:18 (JDIComplexDebuggee.add)
  #2 JDIComplexDebuggee.java:33 (JDIComplexDebuggee.multiply)
  #3 JDIComplexDebuggee.java:62 (JDIComplexDebuggee.factorial)
```

### Commande : `continue`
**Position attendue :** Premier breakpoint atteint (probablement ligne 18, premier `add`)

### Commande : `frame`
**Résultat attendu :**
```
Current frame:
  Method: add
  Class: JDIComplexDebuggee
  Line: 18
  Source: JDIComplexDebuggee.java
```

### Commande : `continue`
**Position attendue :** Deuxième breakpoint atteint (ligne 18, deuxième `add` ou ligne 33)

---

## Résumé des commandes testées

✅ **help** - Phase 1  
✅ **frame** - Toutes les phases  
✅ **method** - Phase 1, 3  
✅ **arguments** - Toutes les phases  
✅ **stack** - Phase 1, 3, 4, 7  
✅ **step-over** - Phase 2, 3, 8  
✅ **step** - Phase 3  
✅ **temporaries** - Phase 2, 3, 4, 7, 8  
✅ **print-var** - Phase 2, 3, 4, 7, 8, 9  
✅ **receiver** - Phase 3  
✅ **sender** - Phase 3, 7  
✅ **receiver-variables** - Phase 3, 5  
✅ **break** - Phase 4, 8, 10  
✅ **breakpoints** - Phase 4, 5, 6, 10  
✅ **continue** - Phase 4, 5, 6, 7, 8, 10  
✅ **break-once** - Phase 6  
✅ **break-on-count** - Phase 5  
✅ **break-before-method-call** - Phase 7