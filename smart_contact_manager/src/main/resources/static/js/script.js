//console.log("Hello world")

// new way of creating function in js````
const toggleSidebar = () => {
	if ($(".sidebar").is(":visible")) {
		// close the sidebar
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	}
	else {
		//show the sidebar````
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
}

// This method handles the code for live change in DOM
search = () => {
	// stores the user input live..
	let pattern = $("#search-input").val();


	if (pattern == "") {
		$(".search-result").hide();

	}
	else {

		//console.log(pattern);

		// sending request to backend api (Rest-Controller)
		let url = `http://localhost:4545/search/${pattern}`;

		fetch(url)
			.then((response) => {
				return response.json();

			})
			.then((data) => {
				/// data
				// console.log(data);
				let text = `<div class='list-group'>`;

				data.forEach((contact) => {
					text += `<a href='/user/contact/${contact.id}' class='list-group-item list-group-item-action'> ${contact.name} </a>`
				});

				text += `</div>`;
				$(".search-result").html(text);
				$(".search-result").show();
			});

	}
}


