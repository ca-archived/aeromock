routing {
	server "vhost1.local", {
		rewrite(/^\/hoge$/, '/hoge/vhost1')
		
		rewrite(/^\/fuga\/(.+)$/, '/fuga/vhost1/$1')
		
		rewrite(/^\/closure\/(.+)\/(.+)/, { _ ->
			"/closure/vhost1/hoge-${_._1}/fuga-${_._2}"
		})
		
		condition QUERY_STRING, /^param1=(.+)&param2=(.+)$/, { _ ->
			rewrite(/(.*)/, { __ ->
				"${__._1}/${_._1}/${_._2}"
			})
		}
	}

	server "vhost2.local", {
		rewrite(/^\/hoge$/, '/hoge/vhost2')
		
		rewrite(/^\/fuga\/(.+)$/, '/fuga/vhost2/$1')
		
		rewrite(/^\/closure\/(.+)\/(.+)/, { _ ->
			"/closure/vhost2/hoge-${_._1}/fuga-${_._2}"
		})
		
		condition QUERY_STRING, /^param1=(.+)&param2=(.+)$/, { _ ->
			rewrite(/(.*)/, { __ ->
				"${__._1}/${_._1}/${_._2}"
			})
		}
	}
}
