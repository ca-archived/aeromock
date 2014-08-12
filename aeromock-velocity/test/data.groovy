if (REQUEST_URI.startsWith("/with_common")) {
    return ["__common", "__enum"]
} else {
    return ["__enum"]
}
