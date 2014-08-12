if (argument == null || argument.length() == 0) {
    throw new IllegalArgumentException("Argument required")
}
return "http://${HOST}${argument}"
