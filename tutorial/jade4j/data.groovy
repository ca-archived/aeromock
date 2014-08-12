if (USER_AGENT =~ /(iPhone|Android)/ ) {
    return ["common/common_sp"]
} else {
    return ["common/common_pc"]
}
