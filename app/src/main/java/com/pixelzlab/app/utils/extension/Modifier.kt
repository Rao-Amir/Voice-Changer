package com.pixelzlab.app.utils.extension

import androidx.compose.ui.Modifier

/**
 * Created by pixelzlab on 31/03/2023.
 */

/**
 * https://medium.com/@aris.kotsomitopoulos/multiple-conditional-modifier-in-jetpack-compose-fe6c18ad359
 */

fun Modifier.thenIf(condition: Boolean, modifier: Modifier.() -> Modifier) =
  if (condition) {
    then(modifier(Modifier))
  } else {
    this
  }

fun Modifier.thenIf(
  condition: Boolean,
  vararg modifier: Modifier.() -> Modifier
): Modifier =
  if (condition) {
    var all: Modifier = Modifier
    modifier.forEach {
      all = all.then(it(Modifier))
    }
    then(all)
  } else {
    this
  }


inline fun <T> T.applyIf(condition: Boolean, run: T.() -> T): T {
  return if (condition) run() else this
}

inline fun <T> T.applyIf(conditionBuilder: (receiver: T) -> Boolean, run: T.() -> T): T {
  return if (conditionBuilder(this)) run() else this
}