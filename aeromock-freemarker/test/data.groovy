if (REQUEST_URI.startsWith("/with_common")) {
    return ["__common", "__enum", "__enum2"]
} else {
    return ["__enum", "__enum2"]
}
