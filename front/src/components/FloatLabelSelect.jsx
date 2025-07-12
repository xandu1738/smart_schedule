import React, { useState } from "react";

const FloatLabelSelect = ({
  label,
  name,
  value,
  onChange,
  id,
  options = [],
  placeholder = "Select an option",
}) => {
  const [isFocused, setIsFocused] = useState(false);

  const shouldFloat = isFocused || value;

  return (
    <div className="relative w-full m-2">
      <select
        id={id || name}
        name={name}
        value={value}
        onChange={onChange}
        onFocus={() => setIsFocused(true)}
        onBlur={() => setIsFocused(false)}
        className="peer w-full border-2 border-slate-300 rounded-lg px-3 pt-3 pb-2 text-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 bg-white appearance-none cursor-pointer"
      >
        <option value="" disabled hidden>
          {placeholder}
        </option>
        {options.map((option, index) => (
          <option key={index} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
      <label
        htmlFor={id || name}
        className={`absolute left-3 px-1 bg-white transition-all duration-200 text-gray-500 ${
          shouldFloat
            ? "top-0 -translate-y-1/2 text-xs text-blue-500"
            : "top-3.5 text-base text-gray-400"
        }`}
        style={{
          pointerEvents: "none",
        }}
      >
        {label}
      </label>
      {/* Custom dropdown arrow */}
      <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none">
        <svg
          className="w-4 h-4 text-gray-400"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M19 9l-7 7-7-7"
          />
        </svg>
      </div>
    </div>
  );
};

export default FloatLabelSelect;
