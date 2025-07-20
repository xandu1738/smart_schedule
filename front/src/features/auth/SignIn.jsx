import { useState } from "react";
// import { Button } from "@/components/ui/button";
// import { Input } from "@/components/ui/input";
// import { Label } from "@/components/ui/label";
// import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Calendar, Eye, EyeOff } from "lucide-react";
import { Link, useNavigate } from "react-router";
import { APP_ROUTE } from "../../config/route.config";
import { useDispatch } from "react-redux";
import { loginSuccess } from "../../helpers/redux/slices/authSlice";
import { authApi } from "../../helpers/redux/slices/extendedApis/authApi";
import { APP_CONFIG } from "../../config/app.config";
import Button from "../../components/Button";
import Spinner from "../../components/Spinner";

export default function SignIn() {
  const [showPassword, setShowPassword] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();
    setIsLoading(true);
    setErrorMessage("");

    authApi
      .login(formData)
      .then((res) => {
        dispatch(
          loginSuccess({
            ...res?.data?.returnObject,
          })
        );
        navigate(`/${APP_ROUTE.DASHBOARD}`);
      })
      .catch((err) => {
        setErrorMessage(err?.message);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center p-4">
      <div className="w-full max-w-md rounded-lg bg-card text-card-foreground shadow-lg bg-white">
        <div className="text-center flex flex-col space-y-1.5 p-6">
          <div className="flex items-center justify-center mb-4">
            <div className="w-12 h-12 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-lg flex items-center justify-center">
              <Calendar className="w-7 h-7 text-white" />
            </div>
          </div>
          <div className="text-2xl font-bold leading-none tracking-tight">
            Welcome back
          </div>
          <div className="text-sm text-muted-foreground">
            Sign in to your {APP_CONFIG.TITLE} account
          </div>
        </div>
        <div className="p-6 pt-0">
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <label htmlFor="email" className="flex justify-start">
                Email
              </label>
              <input
                id="email"
                name="email"
                type="email"
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-base ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
                placeholder="Enter your email"
                value={formData.email}
                onChange={handleInputChange}
                required
              />
            </div>
            <div className="space-y-2">
              <label htmlFor="password" className="flex justify-start">
                Password
              </label>
              <div className="relative">
                <input
                  id="password"
                  name="password"
                  type={showPassword ? "text" : "password"}
                  className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-base ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium file:text-foreground placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 md:text-sm"
                  placeholder="Enter your password"
                  value={formData.password}
                  onChange={handleInputChange}
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-700"
                >
                  {showPassword ? (
                    <EyeOff className="w-4 h-4" />
                  ) : (
                    <Eye className="w-4 h-4" />
                  )}
                </button>
              </div>
              {errorMessage && <p className="text-red-500">{errorMessage}</p>}
            </div>
            <div className="flex items-center justify-between text-sm">
              <Link
                to="/forgot-password"
                className="text-blue-600 hover:text-blue-800 hover:underline"
              >
                Forgot password?
              </Link>
            </div>
            <Button
              type={"submit"}
              buttonName={isLoading ? <Spinner /> : "Sign In"}
              onClick={() => {}}
              className={"w-full"}
              disabled={isLoading}
            />
          </form>
          <div className="mt-6 text-center text-sm">
            <span className="text-gray-600">Don't have an account? </span>
            <Link
              to={APP_ROUTE.SIGN_UP}
              className="text-blue-600 hover:text-blue-800 hover:underline font-medium"
            >
              Sign up
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
