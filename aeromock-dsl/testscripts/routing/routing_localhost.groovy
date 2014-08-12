routing {
	server "localhost", {
		rewrite(/^\/hoge$/, '/hoge/hoge')
	}
}
