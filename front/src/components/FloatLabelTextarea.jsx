import React, { useState } from "react";

const FloatLabelTextarea = ({
  label,
  name,
  value,
  onChange,
  id,
  placeholder = " ",
  className = "",
}) => {
  const [isFocused, setIsFocused] = useState(false);

  const shouldFloat = isFocused || value;

return (
    <div className="relative w-full m-2">
        <input
            type="text"
            id={id || name}
            name={name}
            value={value}
            onChange={onChange}
            onFocus={() => setIsFocused(true)}
            onBlur={() => setIsFocused(false)}
            placeholder={placeholder}
            className={`peer w-full border-2 border-slate-300 rounded-lg px-3 pt-3 pb-2 text-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 min-h-[120px] ${className}`}
        />
        <label
            htmlFor={id || name}
            className={`absolute left-3 px-1 bg-white transition-all duration-200 text-gray-500 ${
                shouldFloat
                    ? "top-2 -translate-y-1/2 text-xs text-blue-500"
                    : "top-5 text-base text-gray-400"
            }`}
            style={{
                pointerEvents: "none",
            }}
        >
            {label}
        </label>
    </div>
);
};

export default FloatLabelTextarea;
