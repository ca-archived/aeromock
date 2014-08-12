if (arguments.isEmpty()) {
  throw new RuntimeException("Argument required")
}

return "http://localhost:3183${arguments[0]}"
