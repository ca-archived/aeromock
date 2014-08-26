if (arguments.isEmpty()) {
  throw new RuntimeException("Argument required")
}

return "http://$HOST${arguments[0]}"
