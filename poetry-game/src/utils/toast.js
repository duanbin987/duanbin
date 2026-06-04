let handler = null

export function setToastHandler(fn) {
  handler = fn
}

export function toast(msg, duration) {
  if (handler) {
    handler(msg, duration)
  }
}
