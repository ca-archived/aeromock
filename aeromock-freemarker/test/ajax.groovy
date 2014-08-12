println("PARAMETERS = $PARAMETERS")
println("FORM_DATA = $FORM_DATA")
if (USER_AGENT =~ /(Android|iPhone|Mac)?/ ) {
  return ["__common"]
} else {
  return ["__common2"]
}