import React from "react";
import * as Icons from "lucide-react";

const LucideIcon = ({ name = "", ...props }) => {
	const IconComponent = Icons[name];

	if (!IconComponent) {
		return <span className="text-red-500">Invalid icon: {name}</span>;
	}

	return <IconComponent {...props} />;
};

export default LucideIcon;
