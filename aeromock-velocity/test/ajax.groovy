if (USER_AGENT =~ /(Android|iPhone|Mac)?/ ) {
  return ["__common"]
} else {
  return ["__common2"]
}